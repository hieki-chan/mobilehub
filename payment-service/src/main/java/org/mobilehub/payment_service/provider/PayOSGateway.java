package org.mobilehub.payment_service.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.exception.InvalidSignatureException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * PayOS REAL adapter (prod-only).
 * - Create payment link via /v2/payment-requests
 * - Verify webhook signature via checksumKey
 *
 * Lưu ý signature cho create payment link:
 *   dataToSign CHỈ gồm 5 field (sort alphabet):
 *   amount, cancelUrl, description, orderCode, returnUrl
 *   -> KHÔNG ký expiredAt/items.
 */
@Slf4j
@Component
public class PayOSGateway implements PaymentProviderGateway {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(1);

    private final PayOSProperties props;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public PayOSGateway(PayOSProperties props) {
        this.props = props;
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    @Override
    public CreateResult createIntent(
            Long orderCode,
            BigDecimal amount,
            String currency,
            String channel,
            String returnUrl
    ) {
        final int amt = toVndInt(amount);

        String finalReturnUrl = StringUtils.hasText(returnUrl)
                ? returnUrl
                : appendOrderCode(props.getReturnUrlBase(), orderCode);

        String cancelUrl = StringUtils.hasText(props.getCancelUrlBase())
                ? appendOrderCode(props.getCancelUrlBase(), orderCode)
                : finalReturnUrl;

        // PayOS giới hạn description ≤ 25 ký tự
        String description = buildShortDescription(orderCode);

        // TTL cho link (gửi trong body, KHÔNG ký)
        long expiredAt = Instant.now().plus(DEFAULT_TTL).getEpochSecond();

        // ==== Sign data (CHỈ 5 FIELD CHUẨN) ====
        Map<String, Object> signData = new HashMap<>();
        signData.put("amount", amt);
        signData.put("cancelUrl", cancelUrl);
        signData.put("description", description);
        signData.put("orderCode", orderCode);
        signData.put("returnUrl", finalReturnUrl);

        String dataToSign = buildDataToSignString(signData);
        String signature = hmacSha256Hex(props.getChecksumKey(), dataToSign);

        if (log.isDebugEnabled()) {
            log.debug("[payos] dataToSign={}", dataToSign);
            log.debug("[payos] signature={}", signature);
        }

        // ==== Request body ====
        Map<String, Object> body = new HashMap<>(signData);
        body.put("expiredAt", expiredAt);     // vẫn gửi để set TTL
        body.put("signature", signature);
        body.put("items", List.of());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, buildHeaders());
        String url = props.getApiBaseUrl() + "/v2/payment-requests";

        final ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientResponseException ex) {
            throw new RuntimeException("PayOS createIntent HTTP " + ex.getRawStatusCode()
                    + " body=" + ex.getResponseBodyAsString(), ex);
        }

        String respBody = resp.getBody();
        if (!resp.getStatusCode().is2xxSuccessful() || respBody == null) {
            throw new RuntimeException("PayOS createIntent HTTP error: " + resp.getStatusCode());
        }

        if (log.isDebugEnabled()) {
            log.debug("[payos] responseBody={}", respBody);
        }

        return parseCreateIntentResponse(respBody);
    }

    @Override
    public CaptureResult capture(String providerPaymentId, BigDecimal amount) {
        throw new UnsupportedOperationException("PayOS không hỗ trợ manual capture");
    }

    @Override
    public RefundResult refund(String providerPaymentId, BigDecimal amount) {
        throw new UnsupportedOperationException("Refund tự động cần nghiệp vụ payout/chi hộ PayOS");
    }

    @Override
    public WebhookEventParsed parseAndVerifyWebhook(String rawBody, Map<String, String> headers) {
        try {
            JsonNode root = mapper.readTree(rawBody);

            String topCode = root.path("code").asText(null);
            String topDesc = root.path("desc").asText(null);
            boolean success = root.path("success").asBoolean(false);

            JsonNode data = root.path("data");
            if (data.isMissingNode() || data.isNull()) {
                throw new RuntimeException("PayOS webhook thiếu data");
            }

            String signature = root.path("signature").asText("");
            if (!StringUtils.hasText(signature)) {
                throw new InvalidSignatureException();
            }

            // PayOS webhook ký trên object data (sort alphabet, raw value)
            Map<String, Object> dataMap = mapper.convertValue(data, Map.class);
            String expected = signHex(props.getChecksumKey(), dataMap);

            if (!MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            )) {
                throw new InvalidSignatureException();
            }

            Long orderCode = data.path("orderCode").asLong();
            BigDecimal amount = data.path("amount").isNumber()
                    ? data.path("amount").decimalValue()
                    : BigDecimal.ZERO;

            String providerPaymentId = data.path("paymentLinkId").asText("unknown");
            String dataCode = data.path("code").asText(null);
            String dataDesc = data.path("desc").asText(null);

            String statusStr = firstNonBlank(
                    data.path("status").asText(null),
                    root.path("status").asText(null)
            );

            PaymentStatus status;
            String eventType;

            if (StringUtils.hasText(statusStr)) {
                switch (statusStr.toUpperCase(Locale.ROOT)) {
                    case "PAID" -> { status = PaymentStatus.CAPTURED; eventType = "payment_paid"; }
                    case "CANCELLED", "CANCELED" -> { status = PaymentStatus.CANCELED; eventType = "payment_cancelled"; }
                    case "PENDING", "PROCESSING" -> { status = PaymentStatus.REQUIRES_ACTION; eventType = "payment_pending"; }
                    case "FAILED", "EXPIRED", "UNDERPAID" -> { status = PaymentStatus.FAILED; eventType = "payment_failed"; }
                    default -> {
                        status = success && "00".equals(dataCode)
                                ? PaymentStatus.CAPTURED
                                : PaymentStatus.FAILED;
                        eventType = "payment_update";
                    }
                }
            } else {
                status = (success && "00".equals(dataCode)) ? PaymentStatus.CAPTURED : PaymentStatus.FAILED;
                eventType = (status == PaymentStatus.CAPTURED) ? "payment_paid" : "payment_failed";
            }

            String eventId = firstNonBlank(
                    data.path("reference").asText(null),
                    providerPaymentId,
                    sha256(rawBody).substring(0, 20)
            );

            return new WebhookEventParsed(
                    eventId,
                    providerPaymentId,
                    orderCode,
                    status,
                    amount,
                    Instant.now(),
                    eventType,
                    firstNonBlank(dataCode, topCode),
                    firstNonBlank(dataDesc, topDesc),
                    "PAYOS"
            );

        } catch (InvalidSignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse PayOS webhook", e);
        }
    }

    // =========================================================
    // CreateIntent helpers
    // =========================================================

    private int toVndInt(BigDecimal amount) {
        try {
            return amount.intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("PayOS amount phải là số nguyên (VND). amount=" + amount);
        }
    }

    /** PayOS description max 25 chars */
    private String buildShortDescription(Long orderCode) {
        String d = "TT don " + orderCode; // ~19-22 chars tùy orderCode
        if (d.length() <= 25) return d;
        return d.substring(0, 25);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", props.getClientId());
        headers.set("x-api-key", props.getApiKey());
        return headers;
    }

    private CreateResult parseCreateIntentResponse(String body) {
        try {
            JsonNode root = mapper.readTree(body);
            String code = root.path("code").asText();
            String desc = root.path("desc").asText();

            if (!"00".equals(code)) {
                throw new RuntimeException("PayOS createIntent failed: code=" + code
                        + ", desc=" + desc + ", body=" + body);
            }

            JsonNode data = root.path("data");
            String paymentLinkId = data.path("paymentLinkId").asText(null);
            String checkoutUrl = data.path("checkoutUrl").asText(null);

            if (!StringUtils.hasText(paymentLinkId) || !StringUtils.hasText(checkoutUrl)) {
                throw new RuntimeException("PayOS response thiếu paymentLinkId/checkoutUrl, body=" + body);
            }

            return new CreateResult(
                    paymentLinkId,
                    checkoutUrl,
                    null,
                    PaymentStatus.REQUIRES_ACTION
            );
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse PayOS createIntent response: " + body, e);
        }
    }

    // =========================================================
    // Sign helpers (PayOS yêu cầu sort key, ký RAW value)
    // =========================================================

    private String signHex(String checksumKey, Map<String, Object> data) {
        String dataQueryStr = buildDataToSignString(data);
        return hmacSha256Hex(checksumKey, dataQueryStr);
    }

    /**
     * Build dataToSign:
     *  - sort key alphabet
     *  - key=value&key2=value2...
     *  - dùng RAW value (KHÔNG encode), để khớp 100% body.
     */
    private String buildDataToSignString(Map<String, Object> data) {
        Map<String, Object> sorted = new TreeMap<>(data);
        List<String> pairs = new ArrayList<>();

        for (Map.Entry<String, Object> e : sorted.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();

            String valueStr;
            if (v == null || "null".equals(v) || "undefined".equals(v)) {
                valueStr = "";
            } else if (v instanceof List<?> list) {
                // normalize list item order nếu là map
                List<Object> normalized = new ArrayList<>();
                for (Object it : list) {
                    normalized.add(it instanceof Map<?, ?> m ? new TreeMap<>(m) : it);
                }
                try {
                    valueStr = mapper.writeValueAsString(normalized);
                } catch (Exception ex) {
                    valueStr = "[]";
                }
            } else {
                valueStr = String.valueOf(v);
            }

            pairs.add(k + "=" + valueStr);
        }
        return String.join("&", pairs);
    }

    private String hmacSha256Hex(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign PayOS data", e);
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

    private static String appendOrderCode(String base, Long orderCode) {
        if (!StringUtils.hasText(base)) return "";
        return base.contains("?")
                ? base + "&orderCode=" + orderCode
                : base + "?orderCode=" + orderCode;
    }
}
