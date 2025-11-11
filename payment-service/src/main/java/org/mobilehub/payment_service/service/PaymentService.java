package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.entity.*;
import org.mobilehub.payment_service.dto.*;
import org.mobilehub.payment_service.exception.NotFoundException;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    public static final String IDEM_ENDPOINT_CREATE = "/api/payments/intents";

    private final PaymentRepository paymentRepo;
    private final PaymentCaptureRepository captureRepo;
    private final PaymentRefundRepository refundRepo;
    private final IdempotencyService idemSvc;
    private final PaymentProviderGateway gateway;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CreateIntentResponse createIntent(CreateIntentRequest req, String idemKey) {
        String reqHash = HashingUtils.sha256(serialize(req));
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = idemSvc.lookupPaymentId(idemKey, IDEM_ENDPOINT_CREATE, reqHash);
            if (existing.isPresent()) {
                var p = paymentRepo.findById(existing.get()).orElseThrow(() -> new NotFoundException("Payment not found"));
                return toCreateResponse(p);
            }
        }
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

        var res = gateway.createIntent(p.getOrderCode(), p.getAmount(), p.getCurrency(), req.channel(), req.returnUrl());
        p.setProviderPaymentId(res.providerPaymentId());
        p.setClientSecret(res.clientSecret());
        p.setStatus(res.nextStatus());
        paymentRepo.save(p);

        if (idemKey != null && !idemKey.isBlank()) {
            idemSvc.storePaymentMapping(idemKey, IDEM_ENDPOINT_CREATE, reqHash, p.getId());
        }

        return new CreateIntentResponse(p.getId(), p.getOrderCode(), p.getStatus(), res.paymentUrl(), res.clientSecret(), p.getProviderPaymentId());
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getStatus(Long orderCode) {
        var p = paymentRepo.findByOrderCode(orderCode).orElseThrow(() -> new NotFoundException("Payment not found"));
        return new PaymentStatusResponse(p.getId(), p.getOrderCode(), p.getStatus(), p.getAmount(), p.getCapturedAmount(), p.getProviderPaymentId());
    }

    @Transactional
    public PaymentStatusResponse capture(Long paymentId, CaptureRequest req) {
        var p = paymentRepo.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found"));
        var res = gateway.capture(p.getProviderPaymentId(), req.amount());
        p.capture(res.amount());
        paymentRepo.save(p);
        captureRepo.save(org.mobilehub.payment_service.entity.PaymentCapture.builder().payment(p).amount(res.amount()).providerCaptureId(res.providerCaptureId()).build());
        return new PaymentStatusResponse(p.getId(), p.getOrderCode(), p.getStatus(), p.getAmount(), p.getCapturedAmount(), p.getProviderPaymentId());
    }

    @Transactional
    public PaymentStatusResponse refund(RefundRequest req) {
        var p = paymentRepo.findById(req.paymentId()).orElseThrow(() -> new NotFoundException("Payment not found"));
        var res = gateway.refund(p.getProviderPaymentId(), req.amount());
        refundRepo.save(org.mobilehub.payment_service.entity.PaymentRefund.builder().payment(p).amount(res.amount()).status(RefundStatus.SUCCEEDED).providerRefundId(res.providerRefundId()).build());
        // Optionally set REFUNDED/PARTIALLY_REFUNDED
        return new PaymentStatusResponse(p.getId(), p.getOrderCode(), p.getStatus(), p.getAmount(), p.getCapturedAmount(), p.getProviderPaymentId());
    }

    private String serialize(Object o) {
        try { return objectMapper.writeValueAsString(o); } catch(Exception e) { return o.toString(); }
    }

    private CreateIntentResponse toCreateResponse(Payment p) {
        return new CreateIntentResponse(p.getId(), p.getOrderCode(), p.getStatus(), null, p.getClientSecret(), p.getProviderPaymentId());
    }
}
