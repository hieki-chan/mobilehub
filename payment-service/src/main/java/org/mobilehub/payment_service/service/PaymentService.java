package org.mobilehub.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.client.InventoryClient;
import org.mobilehub.payment_service.client.OrderClient;
import org.mobilehub.payment_service.client.UserClient;
import org.mobilehub.payment_service.dto.*;
import org.mobilehub.payment_service.entity.*;
import org.mobilehub.payment_service.exception.NotFoundException;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repository.*;
import org.mobilehub.shared.contracts.notification.PaymentCapturedEvent;
import org.springframework.beans.factory.annotation.Value;
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

    /* ================= CONFIG / CONSTANTS ================= */

    public static final String IDEM_ENDPOINT_CREATE = "/api/payments/intents";

    private static final EnumSet<PaymentStatus> ACTIVE_STATUSES = EnumSet.of(
            PaymentStatus.NEW,
            PaymentStatus.REQUIRES_ACTION,
            PaymentStatus.AUTHORIZED,
            PaymentStatus.PARTIALLY_CAPTURED
    );

    private static final String DEFAULT_CURRENCY = "VND";
    private static final String DEFAULT_CHANNEL  = "QR";
    private static final String PAYOS_PROVIDER   = "PAYOS";

    @Value("${kafka.topics.payment-captured:payment.captured}")
    private String paymentCapturedTopic;

    /* ================= DEPENDENCIES ================= */

    private final PaymentRepository paymentRepo;
    private final PaymentCaptureRepository captureRepo;
    private final PaymentRefundRepository refundRepo;
    private final IdempotencyService idemSvc;
    private final PaymentProviderGateway gateway;

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;
    private final UserClient userClient;

    private final KafkaTemplate<String, PaymentCapturedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /* ================= CREATE INTENT ================= */

    /**
     * Tạo payment intent (idempotent theo idemKey + orderId)
     * userId và userEmail không còn được lưu vào Payment entity
     * Sẽ được lấy từ Order và Identity service khi cần
     */
    @Transactional
    public CreateIntentResponse createIntent(
            CreateIntentRequest req,
            String idemKey,
            String userId,
            String userEmail
    ) {
        validateCreateRequest(req);

        String reqHash = sha256(req);

        /* 1. Idempotency theo idemKey */
        if (idemKey != null && !idemKey.isBlank()) {
            var mappedId = idemSvc.lookupPaymentId(idemKey, IDEM_ENDPOINT_CREATE, reqHash);
            if (mappedId.isPresent()) {
                return paymentRepo.findById(mappedId.get())
                        .map(this::toCreateResponse)
                        .orElseThrow(() -> new NotFoundException("Payment not found"));
            }
        }

        /* 2. Idempotency chính theo orderId */
        var latestOpt = paymentRepo.findTopByOrderIdOrderByIdDesc(req.orderId());
        if (latestOpt.isPresent()) {
            Payment latest = latestOpt.get();

            if (latest.getStatus() == PaymentStatus.CAPTURED
                    || ACTIVE_STATUSES.contains(latest.getStatus())) {
                return toCreateResponse(latest);
            }
        }

        /* 3. Chặn trùng orderCode PayOS */
        paymentRepo.findByOrderCode(req.orderCode())
                .ifPresent(p -> {
                    throw new IllegalStateException("Payment already exists for orderCode=" + req.orderCode());
                });

        /* 4. Persist NEW payment - không lưu userId và userEmail */
        Payment payment = Payment.builder()
                .orderId(req.orderId())
                .orderCode(req.orderCode())
                .amount(req.amount())
                .currency(DEFAULT_CURRENCY)
                .provider(PAYOS_PROVIDER)
                .captureMethod(CaptureMethod.AUTOMATIC)
                .status(PaymentStatus.NEW)
                .capturedAmount(BigDecimal.ZERO)
                .build();
        paymentRepo.save(payment);

        /* 5. Gọi PayOS */
        var providerRes = gateway.createIntent(
                payment.getOrderCode(),
                payment.getAmount(),
                payment.getCurrency(),
                DEFAULT_CHANNEL,
                req.returnUrl()
        );

        /* 6. Update theo provider response */
        payment.setProviderPaymentId(providerRes.providerPaymentId());
        payment.setClientSecret(providerRes.clientSecret());
        payment.setPaymentUrl(providerRes.paymentUrl());
        payment.setStatus(providerRes.nextStatus());
        paymentRepo.save(payment);

        /* 7. Lưu idempotency map */
        if (idemKey != null && !idemKey.isBlank()) {
            idemSvc.storePaymentMapping(idemKey, IDEM_ENDPOINT_CREATE, reqHash, payment.getId());
        }

        return toCreateResponse(payment);
    }

    /* ================= QUERY STATUS ================= */

    @Transactional(readOnly = true)
    public PaymentStatusResponse getStatus(Long orderCode) {
        Payment p = paymentRepo.findByOrderCode(orderCode)
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

    /* ================= CAPTURE (NON-PAYOS) ================= */

    @Transactional
    public PaymentStatusResponse capture(Long paymentId, CaptureRequest req) {
        Payment p = paymentRepo.findById(paymentId)
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

        if (p.getStatus() == PaymentStatus.CAPTURED) {
            commitInventoryForOrder(p.getOrderId());
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

    /* ================= REFUND ================= */

    @Transactional
    public PaymentStatusResponse refund(RefundRequest req) {
        Payment p = paymentRepo.findById(req.paymentId())
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (PAYOS_PROVIDER.equalsIgnoreCase(p.getProvider())) {
            throw new UnsupportedOperationException("PayOS refund chưa hỗ trợ tự động");
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

    /* ================= INTERNAL HELPERS ================= */

    private void publishPaymentCapturedAfterCommit(Payment p) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publishPaymentCaptured(p);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publishPaymentCaptured(p);
            }
        });
    }

    private void publishPaymentCaptured(Payment p) {
        // ✅ LUÔN lấy userId từ Order để đảm bảo chính xác
        String userId = fetchUserIdFromOrder(p.getOrderId());
        
        if (userId == null || userId.isBlank()) {
            log.warn("[payment.kafka] Cannot fetch userId from order, skip publish paymentId={}, orderId={}", 
                    p.getId(), p.getOrderId());
            return;
        }

        // ✅ Lấy email từ identity-service
        String userEmail = fetchUserEmail(userId);

        PaymentCapturedEvent evt = new PaymentCapturedEvent(
                p.getOrderId(),
                userId,
                p.getCapturedAmount(),
                p.getCurrency(),
                userEmail
        );

        kafkaTemplate.send(paymentCapturedTopic, String.valueOf(p.getOrderId()), evt);
        log.info("[payment.kafka] published topic={}, orderId={}, userId={}, email={}",
                paymentCapturedTopic, evt.orderId(), evt.userId(), userEmail != null ? userEmail : "null");
    }

    private void commitInventoryForOrder(Long orderId) {
        try {
            var res = orderClient.getReservation(orderId);
            if (res.reservationId() == null || res.reservationId().isBlank()) return;
            inventoryClient.commit(res.reservationId());
        } catch (Exception ex) {
            log.error("Commit inventory failed for orderId={}", orderId, ex);
            throw new RuntimeException("Commit inventory failed", ex);
        }
    }

    /**
     * Lấy userId từ Order khi không có trong header
     */
    private String fetchUserIdFromOrder(Long orderId) {
        if (orderId == null) return null;
        try {
            var orderDto = orderClient.getOrder(orderId);
            if (orderDto != null && orderDto.userId() != null) {
                return String.valueOf(orderDto.userId());
            }
            log.warn("[payment] Cannot fetch userId from order orderId={}", orderId);
            return null;
        } catch (Exception ex) {
            log.error("[payment] Failed to fetch userId from order orderId={}, error={}", 
                    orderId, ex.getMessage());
            return null;
        }
    }

    /**
     * Lấy email từ UserClient khi không có trong header
     */
    private String fetchUserEmail(String userId) {
        if (userId == null || userId.isBlank()) return null;
        try {
            Long userIdLong = Long.parseLong(userId);
            var userDto = userClient.getUser(userIdLong);
            if (userDto != null && userDto.email() != null) {
                return userDto.email();
            }
            log.warn("[payment] Cannot fetch email from user-service userId={}", userId);
            return null;
        } catch (Exception ex) {
            log.error("[payment] Failed to fetch email from user-service userId={}, error={}", 
                    userId, ex.getMessage());
            return null;
        }
    }

    private void validateCreateRequest(CreateIntentRequest req) {
        if (req == null) throw new IllegalArgumentException("Request is null");
        if (req.orderId() == null) throw new IllegalArgumentException("orderId is required");
        if (req.orderCode() == null) throw new IllegalArgumentException("orderCode is required");
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be > 0");
    }

    private String sha256(Object o) {
        try {
            return HashingUtils.sha256(objectMapper.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Serialize request failed", e);
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
