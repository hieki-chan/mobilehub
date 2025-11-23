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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    // --------------------------------------------------------------------
    // CREATE INTENT
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

        // 2) Idempotency theo orderCode
        var existedByOrder = paymentRepo.findByOrderCode(req.orderCode());
        if (existedByOrder.isPresent()) {
            return toCreateResponse(existedByOrder.get());
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

        // 4) Gọi PSP (mock PayOS)
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

        // 5) Lưu idempotency map
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

    // --------------------------------------------------------------------
    // GET STATUS
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
    // CAPTURE MANUAL
    // --------------------------------------------------------------------
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

        // Domain update
        p.capture(res.amount());
        paymentRepo.save(p);

        captureRepo.save(PaymentCapture.builder()
                .payment(p)
                .amount(res.amount())
                .providerCaptureId(res.providerCaptureId())
                .build());

        // Auto commit inventory
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

    // --------------------------------------------------------------------
    // REFUND
    // --------------------------------------------------------------------
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

    // ---------------------------------------------------------
    // FIXED: Build correct UI mock URL for React PayOS
    // ---------------------------------------------------------
    private CreateIntentResponse toCreateResponse(Payment p) {

        // URL FE thanh toán xong quay về
        String returnUrl = "http://localhost:5173/checkout/return?orderCode=" + p.getOrderCode();
        String encodedReturn = URLEncoder.encode(returnUrl, StandardCharsets.UTF_8);

        // UI MOCK PayOS trên React
        String checkoutUrl =
                "http://localhost:5173/mock/payos/checkout"
                        + "?paymentId=" + p.getProviderPaymentId()
                        + "&amount=" + p.getAmount()
                        + "&returnUrl=" + encodedReturn;

        return new CreateIntentResponse(
                p.getId(),
                p.getOrderCode(),
                p.getStatus(),
                checkoutUrl,
                p.getClientSecret(),
                p.getProviderPaymentId()
        );
    }

}
