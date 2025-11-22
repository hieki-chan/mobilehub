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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    public static final String IDEM_ENDPOINT_CREATE = "/api/payments/intents";

    private final PaymentRepository paymentRepo;
    private final PaymentCaptureRepository captureRepo;
    private final PaymentRefundRepository refundRepo;
    private final IdempotencyService idemSvc;
    private final PaymentProviderGateway gateway;

    // ✅ thêm 2 client để sync inventory theo Option 1
    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CreateIntentResponse createIntent(CreateIntentRequest req, String idemKey) {
        String reqHash = HashingUtils.sha256(serialize(req));

        // 1) Idempotency theo idemKey (nếu client gửi)
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = idemSvc.lookupPaymentId(idemKey, IDEM_ENDPOINT_CREATE, reqHash);
            if (existing.isPresent()) {
                var p = paymentRepo.findById(existing.get())
                        .orElseThrow(() -> new NotFoundException("Payment not found"));
                return toCreateResponse(p);
            }
        }

        // 2) Idempotency theo orderCode (bắt buộc để tránh tạo 2 payment cho 1 order)
        var existedByOrder = paymentRepo.findByOrderCode(req.orderCode());
        if (existedByOrder.isPresent()) {
            var p = existedByOrder.get();
            // luôn trả lại intent cũ để tránh nổ unique constraint hoặc tạo double payment
            return toCreateResponse(p);
        }

        // 3) Tạo payment mới
        Payment p = Payment.builder()
                .orderCode(req.orderCode())
                .amount(req.amount())
                .currency(req.currency())
                .status(PaymentStatus.NEW)
                .captureMethod(req.captureMethod())
                .provider(req.provider() != null ? req.provider() : "PAYOS")
                .capturedAmount(BigDecimal.ZERO)
                .build();
        paymentRepo.save(p);

        // 4) Call PSP create intent
        var res = gateway.createIntent(
                p.getOrderCode(),
                p.getAmount(),
                p.getCurrency(),
                req.channel(),
                req.returnUrl()
        );

        p.setProviderPaymentId(res.providerPaymentId());
        p.setClientSecret(res.clientSecret());
        p.setStatus(res.nextStatus());
        paymentRepo.save(p);

        // 5) store mapping idemKey nếu có
        if (idemKey != null && !idemKey.isBlank()) {
            idemSvc.storePaymentMapping(idemKey, IDEM_ENDPOINT_CREATE, reqHash, p.getId());
        }

        return new CreateIntentResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                res.paymentUrl(),
                res.clientSecret(),
                p.getProviderPaymentId()
        );
    }

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

    /**
     * Capture MANUAL:
     * - chỉ cho phép khi captureMethod=MANUAL
     * - chỉ AUTHORIZED / PARTIALLY_CAPTURED mới capture tiếp
     * - không over-capture
     * - khi CAPTURED xong => commit inventory (Option 1)
     */
    @Transactional
    public PaymentStatusResponse capture(Long paymentId, CaptureRequest req) {
        var p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

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

        // Domain cập nhật capturedAmount + status
        p.capture(res.amount());
        paymentRepo.save(p);

        captureRepo.save(PaymentCapture.builder()
                .payment(p)
                .amount(res.amount())
                .providerCaptureId(res.providerCaptureId())
                .build());

        // ✅ Option 1: nếu sau capture mà final CAPTURED thì commit inventory
        if (p.getStatus() == PaymentStatus.CAPTURED) {
            commitInventoryForOrder(p.getOrderCode());
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

    /**
     * Refund không đụng inventory trong Option 1 (stock đã trừ),
     * chỉ cập nhật payment + lưu refund record.
     */
    @Transactional
    public PaymentStatusResponse refund(RefundRequest req) {
        var p = paymentRepo.findById(req.paymentId())
                .orElseThrow(() -> new NotFoundException("Payment not found"));

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

        // Nếu bạn có domain method refund(...) thì gọi ở đây để update refundedAmount/status
        // p.refund(res.amount());
        // paymentRepo.save(p);

        return new PaymentStatusResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                p.getAmount(),
                p.getCapturedAmount(),
                p.getProviderPaymentId()
        );
    }

    // ===================== helpers =====================

    private void commitInventoryForOrder(Long orderId) {
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
            // Cho phép retry capture endpoint nếu commit fail
            log.error("[payment] commit inventory failed for orderId={}, reason={}", orderId, ex.getMessage());
            throw new RuntimeException("Commit inventory failed: " + ex.getMessage(), ex);
        }
    }

    private String serialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return o.toString();
        }
    }

    private CreateIntentResponse toCreateResponse(Payment p) {
        return new CreateIntentResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                null,
                p.getClientSecret(),
                p.getProviderPaymentId()
        );
    }
}
