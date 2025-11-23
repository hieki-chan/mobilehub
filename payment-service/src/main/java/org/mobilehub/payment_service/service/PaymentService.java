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

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PAYOS_PROVIDER = "PAYOS";
    private static final String DEFAULT_CURRENCY = "VND";
    private static final String DEFAULT_CHANNEL = "QR"; // interface yêu cầu, PayOS gateway ignore

    // --------------------------------------------------------------------
    // CREATE INTENT (PAYOS REAL, DTO mới)
    // --------------------------------------------------------------------
    @Transactional
    public CreateIntentResponse createIntent(CreateIntentRequest req, String idemKey) {

        String reqHash = HashingUtils.sha256(serialize(req));

        // 1) Idempotency bằng idempotency key
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = idemSvc.lookupPaymentId(idemKey, IDEM_ENDPOINT_CREATE, reqHash);
            if (existing.isPresent()) {
                var p = paymentRepo.findById(existing.get())
                        .orElseThrow(() -> new NotFoundException("Payment not found"));
                return toCreateResponse(p);
            }
        }

        // 2) Idempotency theo orderCode (PayOS code)
        var existedByOrderCode = paymentRepo.findByOrderCode(req.orderCode());
        if (existedByOrderCode.isPresent()) {
            return toCreateResponse(existedByOrderCode.get());
        }

        // 3) Tạo payment mới (PayOS prod-only)
        Payment p = Payment.builder()
                .orderId(req.orderId())        // ✅ NEW: lưu orderId nội bộ để commit inventory
                .orderCode(req.orderCode())    // ✅ PayOS code (paymentCode)
                .amount(req.amount())
                .currency(DEFAULT_CURRENCY)                 // ✅ set cứng VND
                .status(PaymentStatus.NEW)
                .captureMethod(CaptureMethod.AUTOMATIC)    // ✅ PayOS luôn automatic
                .provider(PAYOS_PROVIDER)                  // ✅ PayOS prod-only
                .capturedAmount(BigDecimal.ZERO)
                .build();
        paymentRepo.save(p);

        // 4) Gọi PayOS REAL create payment link
        var res = gateway.createIntent(
                p.getOrderCode(),
                p.getAmount(),
                p.getCurrency(),       // = VND
                DEFAULT_CHANNEL,       // = QR (không ảnh hưởng)
                req.returnUrl()        // optional, gateway fallback nếu null/blank
        );

        // 5) Update payment theo kết quả provider
        p.setProviderPaymentId(res.providerPaymentId());
        p.setClientSecret(res.clientSecret());
        p.setStatus(res.nextStatus());

        // ✅ Lưu checkoutUrl thật để idempotency trả lại đúng
        p.setPaymentUrl(res.paymentUrl());

        paymentRepo.save(p);

        // 6) Lưu idempotency map
        if (idemKey != null && !idemKey.isBlank()) {
            idemSvc.storePaymentMapping(idemKey, IDEM_ENDPOINT_CREATE, reqHash, p.getId());
        }

        return new CreateIntentResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                p.getPaymentUrl(),
                p.getClientSecret(),
                p.getProviderPaymentId()
        );
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
    // CAPTURE MANUAL -> PAYOS KHÔNG HỖ TRỢ
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

        if (p.getStatus() == PaymentStatus.CAPTURED) {
            // ✅ NEW: commit inventory theo orderId nội bộ
            commitInventoryForOrder(p.getOrderId());
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
    // REFUND -> PAYOS HIỆN TẠI CHƯA HỖ TRỢ AUTO REFUND
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
    private void commitInventoryForOrder(Long orderId) {
        if (orderId == null) {
            log.warn("[payment] orderId is null -> skip commit inventory (old data?)");
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
                p.getPaymentUrl(),
                p.getClientSecret(),
                p.getProviderPaymentId()
        );
    }
}
