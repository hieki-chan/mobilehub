package org.mobilehub.payment_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.entity.Payment;
import org.mobilehub.payment_service.kafka.PaymentEventPublisher;
import org.mobilehub.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.webhooks.WebhookData;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayOSOrderService {

    private final PayOS payOS;               // Bean PayOS đã cấu hình
    private final PaymentRepository payments;
    private final PaymentEventPublisher publisher;
    private final ObjectMapper objectMapper;

    /** Tạo link + lưu Payment(PENDING) */
    @Transactional
    public CreatePaymentLinkResponse createAndSave(Long orderCode,String productName,
                                                   String description,
                                                   Long amount,
                                                   String returnUrl,
                                                   String cancelUrl,
                                                   int quantity) throws PayOSException {

//        long orderCode = System.currentTimeMillis() / 1000; // hoặc nhận từ order-service

        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(productName)
                .price(amount)
                .quantity(quantity)
                .build();

        CreatePaymentLinkRequest req = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item)
                .build();

        CreatePaymentLinkResponse res = payOS.paymentRequests().create(req);

        // Lưu DB (PENDING)
        Payment payment = Payment.builder()
                .orderCode(orderCode)
                .productName(productName)
                .description(description)
                .amount(amount)
                .quantity(quantity)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .checkoutUrl(res.getCheckoutUrl())
                .status("PENDING")
                .build();

        payments.save(payment);

        return res;
    }

    /** Lấy thông tin link từ PayOS (tham khảo) */
    public PaymentLink getFromProvider(Long orderCode) throws PayOSException {
        return payOS.paymentRequests().get(orderCode);
    }

    /** Hủy link trên PayOS (và cập nhật DB) */
    @Transactional
    public PaymentLink cancel(Long orderCode, String reason) throws PayOSException {
        PaymentLink link = payOS.paymentRequests().cancel(orderCode,
                reason == null ? "merchant_cancel" : reason);

        payments.findByOrderCode(orderCode).ifPresent(p -> {
            p.setStatus("CANCELLED");
            payments.save(p);
        });
        return link;
    }

    /**
     * Xác thực webhook + cập nhật DB.
     * Lưu ý: Ở SDK v2, sau verify bạn nhận WebhookData:
     *  - code: "00" nếu thành công
     *  - desc: mô tả
     *  - data: object chứa orderCode, amount, reference... (tùy loại webhook)
     */
    @Transactional
    public WebhookData verifyAndUpdate(String rawBody) throws PayOSException {
        // 1) Verify chữ ký (bắt buộc)
        WebhookData verified = payOS.webhooks().verify(rawBody);

        // 2) Parse JSON (an toàn với Jackson)
        long orderCode;
        String providerCode;
        String desc;
        String reference;

        try {
            JsonNode root = objectMapper.readTree(rawBody);
            providerCode = root.path("code").asText();             // "00" = success
            desc         = root.path("desc").asText();             // "" nếu không có
            JsonNode data = root.path("data");
            orderCode    = data.path("orderCode").asLong(0L);
            reference    = data.path("reference").asText("");      // "" nếu không có
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload webhook không phải JSON hợp lệ", e);
        }

        if (orderCode <= 0) {
            throw new IllegalStateException("orderCode không hợp lệ trong webhook");
        }

        // 3) Lấy payment & kiểm tra idempotency
        Payment p = payments.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalStateException("Order không tồn tại: " + orderCode));

        final String before = p.getStatus() == null ? "PENDING" : p.getStatus().toUpperCase();
        String next;

        boolean success = "00".equals(providerCode);
        if (success) {
            next = "PAID";
        } else if (!desc.isEmpty() && desc.toLowerCase().contains("cancel")) {
            next = "CANCELLED";
        } else {
            next = "FAILED";
        }

        // Không hạ cấp từ PAID; và chỉ publish khi có chuyển trạng thái thực sự
        if ("PAID".equals(before)) {
            p.setLastWebhookRaw(rawBody);
            payments.save(p);
            return verified;
        }
        if (before.equals(next)) {
            // Trùng trạng thái -> chỉ lưu raw để audit
            p.setLastWebhookRaw(rawBody);
            payments.save(p);
            return verified;
        }

        // 4) Cập nhật DB
        p.setStatus(next);
        if ("PAID".equals(next)) {
            p.setPaidAt(LocalDateTime.now());
        }
        p.setLastWebhookRaw(rawBody);
        payments.save(p);

        // 5) Publish event theo trạng thái mới (best-effort, không rollback DB nếu lỗi)
        try {
            if ("PAID".equals(next)) {
                publisher.publishSucceeded(orderCode, p.getAmount(), providerCode, reference);
            } else if ("CANCELLED".equals(next)) {
                publisher.publishCanceled(orderCode, p.getAmount(), providerCode, reference);
            } else { // FAILED
                publisher.publishFailed(orderCode, p.getAmount(), providerCode, reference);
            }
        } catch (Exception ex) {
            // log thôi, không throw để tránh rollback transaction DB
            // log.warn("Publish payment event error: {}", ex.getMessage(), ex);
        }

        return verified;
    }

    public ConfirmWebhookResponse confirmWebhook(String webhookUrl) throws PayOSException {
        return payOS.webhooks().confirm(webhookUrl);
    }
}
