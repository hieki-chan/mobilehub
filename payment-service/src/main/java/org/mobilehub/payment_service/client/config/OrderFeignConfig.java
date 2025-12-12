package org.mobilehub.payment_service.client.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class OrderFeignConfig {

    @Bean
    public RequestInterceptor orderInternalAuthInterceptor(
            @Value("${services.order.internal-api-key:}") String apiKey
    ) {
        return template -> {
            if (apiKey != null && !apiKey.isBlank()) {
                template.header("X-Internal-Api-Key", apiKey);
            }
        };
    }
}
