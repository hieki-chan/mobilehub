package org.mobilehub.installment_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.payment-service.url:http://localhost:8084}")
    private String paymentServiceUrl;

    /**
     * Tạo payment intent cho khoản trả trước của hợp đồng trả góp
     */
    public CreatePaymentResponse createDownPayment(Long orderId, Long orderCode, 
                                                   BigDecimal amount, String returnUrl,
                                                   String userId, String userEmail) {
        try {
            String url = paymentServiceUrl + "/payments/intents";
            
            CreatePaymentRequest request = new CreatePaymentRequest(
                    orderId, orderCode, amount, returnUrl
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("X-User-Id", userId);
            headers.set("X-User-Email", userEmail);
            headers.set("Idempotency-Key", "installment-" + orderId);
            
            HttpEntity<CreatePaymentRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<CreatePaymentResponse> response = restTemplate.exchange(
                    url, 
                    HttpMethod.POST, 
                    entity, 
                    CreatePaymentResponse.class
            );
            
            CreatePaymentResponse result = response.getBody();
            log.info("[PAYMENT-CLIENT] Created payment: paymentId={}, orderCode={}, paymentUrl={}", 
                    result != null ? result.paymentId() : null,
                    result != null ? result.orderCode() : null,
                    result != null ? result.paymentUrl() : null);
            
            return result;
        } catch (Exception e) {
            log.error("[PAYMENT-CLIENT] Failed to create payment for orderId={}: {}", 
                    orderId, e.getMessage());
            return null;
        }
    }

    public record CreatePaymentRequest(
            Long orderId,
            Long orderCode,
            BigDecimal amount,
            String returnUrl
    ) {}

    public record CreatePaymentResponse(
            Long paymentId,
            Long orderCode,
            String status,
            String paymentUrl,
            String clientSecret,
            String providerPaymentId
    ) {}
}
