package org.mobilehub.payment_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.dto.ApiResponse;
import org.mobilehub.payment_service.dto.CreatePaymentLinkRequestBody;
import org.mobilehub.payment_service.entity.Payment;
import org.mobilehub.payment_service.repository.PaymentRepository;
import org.mobilehub.payment_service.service.PayOSOrderService;
import org.springframework.web.bind.annotation.*;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.webhooks.WebhookData;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    private final PayOSOrderService payOSOrderService;
    private final PaymentRepository payments;

    @PostMapping("/create")
    public ApiResponse<CreatePaymentLinkResponse> create(@RequestBody @Valid CreatePaymentLinkRequestBody body)
            throws PayOSException {
        var res = payOSOrderService.createAndSave(
                body.getOrderCode(),
                body.getProductName(),
                body.getDescription(),
                body.getAmount(),
                body.getReturnUrl(),
                body.getCancelUrl(),
                body.getQuantity() == null ? 1 : body.getQuantity()
        );
        return ApiResponse.ok(res);
    }

    /** Lấy bản ghi payment trong DB theo orderCode (local) */
    @GetMapping("/{orderCode}")
    public ApiResponse<Payment> getLocal(@PathVariable long orderCode) {
        Optional<Payment> p = payments.findByOrderCode(orderCode);
        return p.map(ApiResponse::ok).orElseGet(() -> ApiResponse.fail("Payment not found"));
    }

    /** Lấy thông tin mới nhất từ PayOS theo orderCode (provider) */
    @GetMapping("/provider/{orderCode}")
    public ApiResponse<PaymentLink> getFromProvider(@PathVariable long orderCode) throws PayOSException {
        return ApiResponse.ok(payOSOrderService.getFromProvider(orderCode));
    }

    /** Huỷ link thanh toán trên PayOS và cập nhật DB */
    @PutMapping("/{orderCode}/cancel")
    public ApiResponse<PaymentLink> cancel(@PathVariable long orderCode,
                                           @RequestParam(required = false) String reason) throws PayOSException {
        return ApiResponse.ok(payOSOrderService.cancel(orderCode, reason));
    }

    /** Webhook PayOS gọi vào: nhận RAW JSON string để verify + cập nhật DB */
    @PostMapping("/webhook")
    public ApiResponse<WebhookData> webhook(@RequestBody String body) throws PayOSException {
        WebhookData data = payOSOrderService.verifyAndUpdate(body);
        return ApiResponse.ok(data);
    }

    @PostMapping("/confirm-webhook")
    public ApiResponse<?> confirmWebhook(@RequestParam("url") String webhookUrl) throws PayOSException {
        return ApiResponse.ok(payOSOrderService.confirmWebhook(webhookUrl));
    }

}
