package org.mobilehub.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.client.InventoryClient;
import org.mobilehub.payment_service.client.OrderClient;
import org.mobilehub.payment_service.dto.*;
import org.mobilehub.payment_service.entity.*;
import org.mobilehub.payment_service.exception.NotFoundException;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repository.*;
import org.mobilehub.shared.contracts.notification.PaymentCapturedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.EnumSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    public static final String IDEM_ENDPOINT_CREATE = "/api/payments/intents";
    private static final String TOPIC_PAYMENT_CAPTURED = "payment.captured";

    private final PaymentRepository paymentRepo;
    private final PaymentCaptureRepository captureRepo;
    private final PaymentRefundRepository refundRepo;
    private final IdempotencyService idemSvc;
    private final PaymentProviderGateway gateway;

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;

    // ✅ Kafka producer (cần spring-kafka + spring.kafka.bootstrap-servers)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PAYOS_PROVIDER   = "PAYOS";
    private static final String DEFAULT_CURRENCY = "VND";
    private static final String DEFAULT_CHANNEL  = "QR";

    // ✅ các trạng thái coi là "đang active" => retry phải trả về cái này
    private static final EnumSet<PaymentStatus> ACTIVE_STATUSES = EnumSet.of(
            PaymentStatus.NEW,
            PaymentStatus.REQUIRES_ACTION,
            PaymentStatus.AUTHORIZED,
            PaymentStatus.PARTIALLY_CAPTURED
    );

    // --------------------------------------------------------------------
    // CREATE INTENT (idempotent theo orderId)
    // --------------------------------------------------------------------
    /**
     * ✅ SỬA: truyền userId/userEmail từ Controller (lấy từ JWT)
     * - userId: BẮT BUỘC để sau này webhook CAPTURED publish event cho notification-service.
     * - userEmail: optional.
     */
    @Transactional
    public CreateIntentResponse createIntent(CreateIntentRequest req, String idemKey, String userId, String userEmail) {

        validateCreateRequest(req);

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        String reqHash = HashingUtils.sha256(serialize(req));

        // 1) Idempotency theo idemKey (giữ nguyên)
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = idemSvc.lookupPaymentId(idemKey, IDEM_ENDPOINT_CREATE, reqHash);
            if (existing.isPresent()) {
                var p = paymentRepo.findById(existing.get())
                        .orElseThrow(() -> new NotFoundException("Payment not found"));
                return toCreateResponse(p);
            }
        }

        // 2) ✅ Idempotency chính theo orderId nội bộ
        //    - Nếu orderId đã có payment ACTIVE hoặc CAPTURED => trả lại luôn
        var latestOpt = paymentRepo.findTopByOrderIdOrderByIdDesc(req.orderId());
        if (latestOpt.isPresent()) {
            Payment latest = latestOpt.get();

            if (latest.getStatus() == PaymentStatus.CAPTURED) {
                return toCreateResponse(latest);
            }

            if (ACTIVE_STATUSES.contains(latest.getStatus())) {
                return toCreateResponse(latest);
            }
            // Nếu latest là FAILED/CANCELED thì cho phép tạo payment mới
        }

        // 3) (Optional) Chặn trùng theo orderCode PayOS
        var existedByOrderCode = paymentRepo.findByOrderCode(req.orderCode());
        if (existedByOrderCode.isPresent()) {
            return toCreateResponse(existedByOrderCode.get());
        }

        // 4) Tạo payment mới
        Payment p = Payment.builder()
                .orderId(req.orderId())        // ✅ orderId nội bộ
                .userId(userId)                // ✅ BẮT BUỘC
                .userEmail(userEmail)          // ✅ optional
                .orderCode(req.orderCode())    // ✅ orderCode gửi sang PayOS
                .amount(req.amount())
                .currency(DEFAULT_CURRENCY)
                .status(PaymentStatus.NEW)
                .captureMethod(CaptureMethod.AUTOMATIC)
                .provider(PAYOS_PROVIDER)
                .capturedAmount(BigDecimal.ZERO)
                .build();
        paymentRepo.save(p);

        // 5) Gọi PayOS tạo payment link
        var res = gateway.createIntent(
                p.getOrderCode(),
                p.getAmount(),
                p.getCurrency(),
                DEFAULT_CHANNEL,
                req.returnUrl()
        );

        // 6) Update theo provider
        p.setProviderPaymentId(res.providerPaymentId());
        p.setClientSecret(res.clientSecret());
        p.setStatus(res.nextStatus());
        p.setPaymentUrl(res.paymentUrl());
        paymentRepo.save(p);

        // 7) Lưu idempotency map (giữ nguyên)
        if (idemKey != null && !idemKey.isBlank()) {
            idemSvc.storePaymentMapping(idemKey, IDEM_ENDPOINT_CREATE, reqHash, p.getId());
        }

        return toCreateResponse(p);
    }

    // --------------------------------------------------------------------
    // GET STATUS (theo PayOS orderCode)
    // --------------------------------------------------------------------
    @Transactional(readOnly = true)
    public PaymentStatusResponse getStatus(Long orderCode) {
        var p = paymentRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        return new PaymentStatusResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                p.getAmount(),
                p.getCapturedAmount(),
                p.getProviderPaymentId()
        );
    }

    // --------------------------------------------------------------------
    // CAPTURE MANUAL -> PAYOS KHÔNG HỖ TRỢ (giữ nguyên)
    // --------------------------------------------------------------------
    @Transactional
    public PaymentStatusResponse capture(Long paymentId, CaptureRequest req) {
        var p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (PAYOS_PROVIDER.equalsIgnoreCase(p.getProvider())) {
            throw new UnsupportedOperationException("PayOS không hỗ trợ manual capture");
        }

        if (p.getCaptureMethod() != CaptureMethod.MANUAL) {
            throw new IllegalStateException("Capture only allowed for MANUAL payments");
        }

        if (p.getStatus() != PaymentStatus.AUTHORIZED
                && p.getStatus() != PaymentStatus.PARTIALLY_CAPTURED) {
            throw new IllegalStateException("Only AUTHORIZED / PARTIALLY_CAPTURED can be captured");
        }

        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Capture amount must be > 0");
        }

        BigDecimal remaining = p.getAmount().subtract(p.getCapturedAmount());
        if (req.amount().compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Over-capture. Remaining=" + remaining);
        }

        var res = gateway.capture(p.getProviderPaymentId(), req.amount());

        p.capture(res.amount());
        paymentRepo.save(p);

        captureRepo.save(PaymentCapture.builder()
                .payment(p)
                .amount(res.amount())
                .providerCaptureId(res.providerCaptureId())
                .build());

        // ✅ nếu capture xong thành CAPTURED -> commit inventory + publish event
        if (p.getStatus() == PaymentStatus.CAPTURED) {
            commitInventoryForOrder(p.getOrderId());

            // publish notification event AFTER_COMMIT
            publishPaymentCapturedAfterCommit(p);
        }

        return new PaymentStatusResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                p.getAmount(),
                p.getCapturedAmount(),
                p.getProviderPaymentId()
        );
    }

    // --------------------------------------------------------------------
    // REFUND (giữ nguyên)
    // --------------------------------------------------------------------
    @Transactional
    public PaymentStatusResponse refund(RefundRequest req) {
        var p = paymentRepo.findById(req.paymentId())
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (PAYOS_PROVIDER.equalsIgnoreCase(p.getProvider())) {
            throw new UnsupportedOperationException("Refund PayOS cần nghiệp vụ payout/chi hộ, chưa hỗ trợ auto");
        }

        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be > 0");
        }

        var res = gateway.refund(p.getProviderPaymentId(), req.amount());

        refundRepo.save(PaymentRefund.builder()
                .payment(p)
                .amount(res.amount())
                .status(RefundStatus.SUCCEEDED)
                .providerRefundId(res.providerRefundId())
                .build());

        return new PaymentStatusResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                p.getAmount(),
                p.getCapturedAmount(),
                p.getProviderPaymentId()
        );
    }

    // --------------------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------------------
    private void publishPaymentCapturedAfterCommit(Payment p) {
        if (p == null) return;

        // userId là BẮT BUỘC nếu bạn muốn notification-service hiển thị theo user
        if (p.getUserId() == null || p.getUserId().isBlank()) {
            log.warn("[payment.kafka] userId missing for paymentId={}, orderId={}, skip publish PaymentCapturedEvent",
                    p.getId(), p.getOrderId());
            return;
        }

        PaymentCapturedEvent evt = new PaymentCapturedEvent(
                p.getOrderId(),
                p.getUserId(),
                p.getCapturedAmount() != null ? p.getCapturedAmount() : BigDecimal.ZERO,
                (p.getCurrency() == null || p.getCurrency().isBlank()) ? DEFAULT_CURRENCY : p.getCurrency(),
                p.getUserEmail()
        );

        Runnable publishTask = () -> {
            try {
                String key = evt.orderId() == null ? null : String.valueOf(evt.orderId());
                kafkaTemplate.send(TOPIC_PAYMENT_CAPTURED, key, evt);
                log.info("[payment.kafka] published topic={}, orderId={}, userId={}, amount={}",
                        TOPIC_PAYMENT_CAPTURED, evt.orderId(), evt.userId(), evt.amount());
            } catch (Exception ex) {
                log.error("[payment.kafka] publish FAILED topic={}, orderId={}, reason={}",
                        TOPIC_PAYMENT_CAPTURED, evt.orderId(), ex.getMessage(), ex);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishTask.run();
                }
            });
        } else {
            publishTask.run();
        }
    }

    private void commitInventoryForOrder(Long orderId) {
        if (orderId == null) {
            log.warn("[payment] orderId is null -> skip commit inventory");
            return;
        }

        try {
            var resDto = orderClient.getReservation(orderId);
            String reservationId = resDto.reservationId();

            if (reservationId == null || reservationId.isBlank()) {
                log.warn("[payment] reservationId missing for orderId={}, skip commit", orderId);
                return;
            }

            inventoryClient.commit(reservationId);
            log.info("[payment] committed inventory reservationId={} for orderId={}", reservationId, orderId);

        } catch (Exception ex) {
            log.error("[payment] commit inventory failed for orderId={}, reason={}", orderId, ex.getMessage());
            throw new RuntimeException("Commit inventory failed: " + ex.getMessage(), ex);
        }
    }

    private void validateCreateRequest(CreateIntentRequest req) {
        if (req == null) throw new IllegalArgumentException("Request is null");
        if (req.orderId() == null) throw new IllegalArgumentException("orderId is required");
        if (req.orderCode() == null) throw new IllegalArgumentException("orderCode is required");
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be > 0");
    }

    private String serialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

    private CreateIntentResponse toCreateResponse(Payment p) {
        return new CreateIntentResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                p.getPaymentUrl(),
                p.getClientSecret(),
                p.getProviderPaymentId()
        );
    }
}
