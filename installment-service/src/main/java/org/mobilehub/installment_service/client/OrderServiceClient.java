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

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.order-service.url:http://localhost:8085}")
    private String orderServiceUrl;

    @Value("${internal.api-key}")
    private String internalApiKey;

    /**
     * Lấy order info từ applicationId
     */
    public OrderBasicDto getOrderByApplicationId(Long applicationId) {
        try {
            String url = orderServiceUrl + "/internal/orders/by-application/" + applicationId;
            
            // Thêm header X-Internal-Api-Key
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Api-Key", internalApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<OrderBasicDto> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    OrderBasicDto.class
            );
            
            OrderBasicDto order = response.getBody();
            log.debug("[ORDER-CLIENT] Retrieved order for applicationId={}, orderId={}, userId={}", 
                    applicationId, order != null ? order.id() : null, order != null ? order.userId() : null);
            return order;
        } catch (Exception e) {
            log.error("[ORDER-CLIENT] Failed to get order for applicationId={}: {}", 
                    applicationId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy userId từ order thông qua applicationId
     */
    public Long getUserIdByApplicationId(Long applicationId) {
        OrderBasicDto order = getOrderByApplicationId(applicationId);
        return order != null ? order.userId() : null;
    }

    public record OrderBasicDto(Long id, Long userId) {}
}
