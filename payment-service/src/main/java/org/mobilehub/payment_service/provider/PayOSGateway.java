package org.mobilehub.payment_service.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.exception.InvalidSignatureException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Component
public class PayOSGateway implements PaymentProviderGateway {

    private final PayOSProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    public PayOSGateway(PayOSProperties props) {
        this.props = props;
    }

    @Override
    public CreateResult createIntent(
            Long orderCode,
            BigDecimal amount,
            String currency,
            String channel,
            String returnUrl
    ) {
        String providerPaymentId = "pay_" + UUID.randomUUID();

        // Phải encode returnUrl để tránh lỗi redirect
        String encodedReturnUrl = java.net.URLEncoder.encode(
                returnUrl,
                java.nio.charset.StandardCharsets.UTF_8
        );

        // React UI mock PayOS
        String paymentUrl =
                props.getBaseUrl()
                        + "/checkout"
                        + "?paymentId=" + providerPaymentId
                        + "&amount=" + amount
                        + "&returnUrl=" + encodedReturnUrl;

        return new CreateResult(
                providerPaymentId,
                paymentUrl,
                null,
                PaymentStatus.REQUIRES_ACTION
        );
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

        // ===== Parse JSON an toàn (có thì lấy, không có thì fallback) =====
        String eventId = sha256(rawBody).substring(0, 20);
        String providerPaymentId = "pay_simulated";
        Long orderCode = null;
        BigDecimal amount = null;
        String eventType = "unknown";
        String errorCode = null;
        String errorMessage = null;
        PaymentStatus status = PaymentStatus.NEW;

        try {
            JsonNode root = mapper.readTree(rawBody);

            // Gợi ý structure phổ biến: root.type / root.eventType
            eventType = firstNonBlank(
                    root.path("type").asText(null),
                    root.path("eventType").asText(null),
                    "unknown"
            );

            // orderCode có thể nằm ở root.orderCode hoặc root.data.orderCode
            orderCode = firstLong(
                    root.path("orderCode"),
                    root.path("data").path("orderCode")
            );

            // providerPaymentId có thể là paymentId / transactionId / data.paymentId
            providerPaymentId = firstNonBlank(
                    root.path("paymentId").asText(null),
                    root.path("transactionId").asText(null),
                    root.path("data").path("paymentId").asText(null),
                    providerPaymentId
            );

            // amount có thể nằm ở root.amount hoặc root.data.amount
            amount = firstBigDecimal(
                    root.path("amount"),
                    root.path("data").path("amount")
            );

            // error info (nếu failed/canceled)
            errorCode = firstNonBlank(
                    root.path("errorCode").asText(null),
                    root.path("code").asText(null),
                    null
            );
            errorMessage = firstNonBlank(
                    root.path("errorMessage").asText(null),
                    root.path("desc").asText(null),
                    root.path("message").asText(null),
                    null
            );

            // Map status theo eventType hoặc field status
            String rawStatus = firstNonBlank(
                    root.path("status").asText(null),
                    eventType,
                    ""
            ).toLowerCase();

            if (rawStatus.contains("authorized") || rawStatus.contains("auth")) {
                status = PaymentStatus.AUTHORIZED;
            } else if (rawStatus.contains("captured") || rawStatus.contains("paid") || rawStatus.contains("success")) {
                status = PaymentStatus.CAPTURED;
            } else if (rawStatus.contains("failed") || rawStatus.contains("fail")) {
                status = PaymentStatus.FAILED;
            } else if (rawStatus.contains("canceled") || rawStatus.contains("cancelled") || rawStatus.contains("cancel")) {
                status = PaymentStatus.CANCELED; // ✅ đúng enum của bạn
            }

        } catch (Exception ignore) {
            // Fallback stub như bạn cũ:
            if (orderCode == null) orderCode = 123L;
            if (amount == null) amount = BigDecimal.ZERO;
            if (eventType == null) eventType = "payment_captured";
            status = PaymentStatus.CAPTURED;
        }

        // ✅ Return record mới có thêm errorCode/errorMessage/provider
        return new WebhookEventParsed(
                eventId,
                providerPaymentId,
                orderCode,
                status,
                amount,
                Instant.now(),
                eventType,
                errorCode,
                errorMessage,
                "PAYOS"
        );
    }

    // ===================== helpers =====================

    private static boolean verifyHmac(String payload, String expectedSignature, String secret) {
        if (!StringUtils.hasText(expectedSignature)) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String actual = Base64.getEncoder().encodeToString(digest);
            return MessageDigest.isEqual(
                    actual.getBytes(StandardCharsets.UTF_8),
                    expectedSignature.getBytes(StandardCharsets.UTF_8)
            );
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

    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static Long firstLong(JsonNode... nodes) {
        for (JsonNode n : nodes) {
            if (n != null && n.isNumber()) return n.asLong();
        }
        return null;
    }

    private static BigDecimal firstBigDecimal(JsonNode... nodes) {
        for (JsonNode n : nodes) {
            if (n != null && n.isNumber()) return n.decimalValue();
            if (n != null && n.isTextual()) {
                try { return new BigDecimal(n.asText()); } catch (Exception ignore) {}
            }
        }
        return null;
    }
}
