package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.client.InventoryClient;
import org.mobilehub.payment_service.client.OrderClient;
import org.mobilehub.payment_service.client.UserClient;
import org.mobilehub.payment_service.entity.Payment;
import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.entity.WebhookEvent;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repository.PaymentRepository;
import org.mobilehub.payment_service.repository.WebhookEventRepository;
import org.mobilehub.shared.contracts.notification.PaymentCapturedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final String TOPIC_PAYMENT_CAPTURED = "payment.captured";

    private final PaymentProviderGateway gateway;
    private final WebhookEventRepository eventRepo;
    private final PaymentRepository paymentRepo;

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;
    private final UserClient userClient;

    private final KafkaTemplate<String, PaymentCapturedEvent> kafkaTemplate;

    @Transactional
    public void handle(String rawBody, Map<String, String> headers) {

        var evt = gateway.parseAndVerifyWebhook(rawBody, headers);

        final String eventId = safeEventId(evt.eventId(), rawBody);

        if (eventRepo.existsByEventId(eventId)) {
            log.info("[payment.webhook] duplicate eventId={}, skip", eventId);
            return;
        }

        // ✅ tìm Payment theo orderCode (PayOS gửi lên) hoặc providerPaymentId
        Payment payment = findPayment(evt.orderCode(), evt.providerPaymentId());

        // ✅ payment chưa tồn tại -> vẫn ACK + lưu event audit
        if (payment == null) {
            saveEventWithoutPayment(eventId, rawBody);
            log.warn("[payment.webhook] payment not found (ACK). orderCode={}, providerPaymentId={}",
                    evt.orderCode(), evt.providerPaymentId());
            return;
        }

        PaymentStatus prevStatus = payment.getStatus();
        boolean needCommit  = false;
        boolean needRelease = false;

        PaymentStatus newStatus = evt.status();

        // ✅ sẽ tạo event nếu thực sự chuyển sang CAPTURED lần đầu
        boolean changedToCaptured = false;

        if (newStatus == PaymentStatus.AUTHORIZED) {
            if (prevStatus != PaymentStatus.AUTHORIZED) {
                payment.markAuthorized();
            }

        } else if (newStatus == PaymentStatus.CAPTURED) {
            if (evt.amount() != null) {
                payment.setCapturedAmount(evt.amount());
            }
            if (prevStatus != PaymentStatus.CAPTURED) {
                payment.setStatus(PaymentStatus.CAPTURED);
                needCommit = true;
                changedToCaptured = true;
            }

        } else if (newStatus == PaymentStatus.FAILED) {
            if (prevStatus != PaymentStatus.FAILED) {
                String code = evt.errorCode() != null ? evt.errorCode() : "webhook_failed";
                String msg  = evt.errorMessage() != null ? evt.errorMessage() : "Provider reported failure";
                payment.fail(code, msg);
                needRelease = true;
            }

        } else if (newStatus == PaymentStatus.CANCELED) {
            if (prevStatus != PaymentStatus.CANCELED) {
                payment.setStatus(PaymentStatus.CANCELED);
                payment.setFailureCode(evt.errorCode() != null ? evt.errorCode() : "webhook_canceled");
                payment.setFailureMessage(evt.errorMessage() != null ? evt.errorMessage() : "Provider reported canceled");
                needRelease = true;
            }

        } else {
            log.info("[payment.webhook] ignore status={} orderCode={}", newStatus, evt.orderCode());
        }

        paymentRepo.save(payment);

        // ✅ QUAN TRỌNG: dùng orderId nội bộ để lấy reservationId
        String reservationId = fetchReservationIdSafe(payment.getOrderId());

        if (reservationId != null && !reservationId.isBlank()) {
            if (needCommit) {
                try {
                    log.info("[payment.webhook] CAPTURED -> commit inventory reservationId={}", reservationId);
                    inventoryClient.commit(reservationId);
                } catch (Exception ex) {
                    log.error("[payment.webhook] commit inventory FAILED reservationId={}, reason={}",
                            reservationId, ex.getMessage(), ex);
                }
            } else if (needRelease) {
                try {
                    log.info("[payment.webhook] FAILED/CANCELED -> release inventory reservationId={}", reservationId);
                    inventoryClient.release(reservationId);
                } catch (Exception ex) {
                    log.error("[payment.webhook] release inventory FAILED reservationId={}, reason={}",
                            reservationId, ex.getMessage(), ex);
                }
            }
        } else if (needCommit || needRelease) {
            log.warn("[payment.webhook] reservationId missing for orderId={}, skip inventory action",
                    payment.getOrderId());
        }

        if (changedToCaptured) {
            PaymentCapturedEvent capturedEvent =
                    buildPaymentCapturedEvent(payment, evt.amount());

            if (capturedEvent != null) {
                publishAfterCommit(capturedEvent);
            }
        }


        // lưu event audit
        WebhookEvent entity = WebhookEvent.builder()
                .eventId(eventId)
                .provider(evt.provider() != null ? evt.provider() : "PAYOS")
                .orderCode(evt.orderCode())
                .eventType(evt.eventType())
                .occurredAt(evt.occurredAt())
                .payloadHash(HashingUtils.sha256(rawBody))
                .payment(payment)
                .build();

        eventRepo.save(entity);

        log.info("[payment.webhook] processed eventId={}, orderId={}, orderCode={}, status={} (prevStatus={})",
                eventId, payment.getOrderId(), payment.getOrderCode(), evt.status(), prevStatus);
    }

    private void publishAfterCommit(PaymentCapturedEvent event) {
        if (event == null) return;

        Runnable publishTask = () -> {
            try {
                String key = event.orderId() == null ? null : String.valueOf(event.orderId());
                kafkaTemplate.send(TOPIC_PAYMENT_CAPTURED, key, event);
                log.info("[payment.kafka] published topic={}, orderId={}, userId={}, email={}, amount={}, currency={}",
                        TOPIC_PAYMENT_CAPTURED, event.orderId(), event.userId(), 
                        event.userEmail() != null ? event.userEmail() : "null",
                        event.amount(), event.currency());
            } catch (Exception ex) {
                log.error("[payment.kafka] publish FAILED topic={}, orderId={}, reason={}",
                        TOPIC_PAYMENT_CAPTURED, event.orderId(), ex.getMessage(), ex);
            }
        };

        // ✅ Ưu tiên bắn sau commit để tránh bắn event khi DB rollback
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishTask.run();
                }
            });
        } else {
            // fallback (hiếm): không có transaction sync
            publishTask.run();
        }
    }

    private PaymentCapturedEvent buildPaymentCapturedEvent(Payment payment, BigDecimal fallbackAmount) {

        // ✅ LUÔN lấy userId từ Order để đảm bảo chính xác
        String userId = fetchUserIdFromOrder(payment.getOrderId());
        
        if (userId == null || userId.isBlank()) {
            log.warn("[payment.webhook] Cannot fetch userId from order orderId={}, skip event",
                    payment.getOrderId());
            return null;
        }

        // ✅ Lấy email từ identity-service
        String userEmail = fetchUserEmail(userId);

        BigDecimal amount = payment.getCapturedAmount();
        if (amount == null) amount = fallbackAmount;
        if (amount == null) amount = BigDecimal.ZERO;

        String currency = payment.getCurrency();
        if (currency == null || currency.isBlank()) currency = "VND";

        return new PaymentCapturedEvent(
                payment.getOrderId(),
                userId,
                amount,
                currency,
                userEmail
        );
    }



    private String tryInvokeString(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object v = m.invoke(target);
            return v != null ? String.valueOf(v) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private BigDecimal tryInvokeBigDecimal(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object v = m.invoke(target);
            if (v == null) return null;
            if (v instanceof BigDecimal bd) return bd;
            // nếu lỡ nó là Long/Integer -> convert
            if (v instanceof Number n) return BigDecimal.valueOf(n.longValue());
            return new BigDecimal(String.valueOf(v));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String safeEventId(String providerEventId, String rawBody) {
        if (providerEventId != null && !providerEventId.isBlank()) return providerEventId;
        return "payload:" + HashingUtils.sha256(rawBody);
    }

    private Payment findPayment(Long orderCode, String providerPaymentId) {
        if (orderCode != null) {
            Optional<Payment> byOrder = paymentRepo.findByOrderCode(orderCode);
            if (byOrder.isPresent()) return byOrder.get();
        }
        if (providerPaymentId != null && !providerPaymentId.isBlank()) {
            Optional<Payment> byProvider = paymentRepo.findByProviderPaymentId(providerPaymentId);
            if (byProvider.isPresent()) return byProvider.get();
        }
        return null;
    }

    private String fetchReservationIdSafe(Long orderId) {
        if (orderId == null) return null;
        try {
            var resDto = orderClient.getReservation(orderId);
            return resDto != null ? resDto.reservationId() : null;
        } catch (Exception ex) {
            log.warn("[payment.webhook] cannot fetch reservationId for orderId={}, reason={}",
                    orderId, ex.getMessage());
            return null;
        }
    }

    /**
     * Lấy userId từ Order
     */
    private String fetchUserIdFromOrder(Long orderId) {
        if (orderId == null) return null;
        try {
            var orderDto = orderClient.getOrder(orderId);
            if (orderDto != null && orderDto.userId() != null) {
                return String.valueOf(orderDto.userId());
            }
            log.warn("[payment.webhook] Cannot fetch userId from order orderId={}", orderId);
            return null;
        } catch (Exception ex) {
            log.error("[payment.webhook] Failed to fetch userId from order orderId={}, error={}", 
                    orderId, ex.getMessage());
            return null;
        }
    }

    /**
     * Lấy email từ identity-service
     */
    private String fetchUserEmail(String userId) {
        if (userId == null || userId.isBlank()) return null;
        try {
            Long userIdLong = Long.parseLong(userId);
            var userDto = userClient.getUser(userIdLong);
            if (userDto != null && userDto.email() != null) {
                return userDto.email();
            }
            log.warn("[payment.webhook] Cannot fetch email from user-service userId={}", userId);
            return null;
        } catch (Exception ex) {
            log.error("[payment.webhook] Failed to fetch email from user-service userId={}, error={}", 
                    userId, ex.getMessage());
            return null;
        }
    }

    private void saveEventWithoutPayment(String eventId, String rawBody) {
        WebhookEvent entity = WebhookEvent.builder()
                .eventId(eventId)
                .provider("PAYOS")
                .orderCode(null)
                .eventType("UNRESOLVED")
                .occurredAt(Instant.now())
                .payloadHash(HashingUtils.sha256(rawBody))
                .payment(null)
                .build();
        eventRepo.save(entity);
    }
}
