package org.mobilehub.payment_service.provider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import org.mobilehub.payment_service.entity.PaymentStatus;

public interface PaymentProviderGateway {

    record CreateResult(String providerPaymentId, String paymentUrl, String clientSecret, PaymentStatus nextStatus) {}

    record CaptureResult(String providerCaptureId, BigDecimal amount, PaymentStatus status) {}

    record RefundResult(String providerRefundId, BigDecimal amount, String status) {}

    record WebhookEventParsed(String eventId, String providerPaymentId, Long orderCode, 
                              PaymentStatus status, BigDecimal amount, Instant occurredAt, 
                              String eventType) {}

    CreateResult createIntent(Long orderCode, BigDecimal amount, String currency, String channel, String returnUrl);

    CaptureResult capture(String providerPaymentId, BigDecimal amount);

    RefundResult refund(String providerPaymentId, BigDecimal amount);

    WebhookEventParsed parseAndVerifyWebhook(String rawBody, Map<String, String> headers);
}
