package org.mobilehub.payment_service.provider;

import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.exception.InvalidSignatureException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Component
public class PayOSGateway implements PaymentProviderGateway {

    private final PayOSProperties props;

    public PayOSGateway(PayOSProperties props) {
        this.props = props;
    }

    @Override
    public CreateResult createIntent(Long orderCode, BigDecimal amount, String currency, String channel, String returnUrl) {
        // Stub: In real life, call external PSP API and return its data
        String providerPaymentId = "pay_" + UUID.randomUUID();
        String paymentUrl = props.getBaseUrl() + "/checkout/" + providerPaymentId + "?returnUrl=" + returnUrl;
        // Assume REQUIRES_ACTION for redirect flows
        return new CreateResult(providerPaymentId, paymentUrl, null, PaymentStatus.REQUIRES_ACTION);
    }

    @Override
    public CaptureResult capture(String providerPaymentId, BigDecimal amount) {
        // Stub: call PSP capture
        return new CaptureResult("cap_" + UUID.randomUUID(), amount, PaymentStatus.CAPTURED);
    }

    @Override
    public RefundResult refund(String providerPaymentId, BigDecimal amount) {
        // Stub: call PSP refund
        return new RefundResult("refund_" + UUID.randomUUID(), amount, "SUCCEEDED");
    }

    @Override
    public WebhookEventParsed parseAndVerifyWebhook(String rawBody, Map<String, String> headers) {
        String sigHeader = headers.getOrDefault(props.getSignatureHeader(), "");
        if (!verifyHmac(rawBody, sigHeader, props.getWebhookSecret())) {
            throw new InvalidSignatureException();
        }
        // For demo: parse minimal fields from a simple JSON structure or simulate a CAPTURED event.
        // In real life, parse JSON library and map accordingly.
        String eventId = sha256(rawBody).substring(0, 20);
        // You would extract these fields from JSON. Here we just simulate.
        Long orderCode = 123L;
        String providerPaymentId = "pay_simulated";
        java.math.BigDecimal amount = new java.math.BigDecimal("0");
        return new WebhookEventParsed(eventId, providerPaymentId, orderCode, PaymentStatus.CAPTURED, amount, Instant.now(), "payment_captured");
    }

    private static boolean verifyHmac(String payload, String expectedSignature, String secret) {
        if (!StringUtils.hasText(expectedSignature)) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String actual = Base64.getEncoder().encodeToString(digest);
            return MessageDigest.isEqual(actual.getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}
