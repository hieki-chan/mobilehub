package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.client.InventoryClient;
import org.mobilehub.payment_service.client.OrderClient;
import org.mobilehub.payment_service.entity.Payment;
import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.entity.WebhookEvent;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repository.PaymentRepository;
import org.mobilehub.payment_service.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentProviderGateway gateway;
    private final WebhookEventRepository eventRepo;
    private final PaymentRepository paymentRepo;

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;

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

        // lưu event
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
