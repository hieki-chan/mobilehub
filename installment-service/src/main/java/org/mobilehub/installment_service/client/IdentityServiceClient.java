package org.mobilehub.installment_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.identity.url:http://localhost:8081}")
    private String identityServiceUrl;

    /**
     * Lấy thông tin user từ identity-service
     */
    public UserDto getUserById(Long userId) {
        try {
            String url = identityServiceUrl + "/internal/users/" + userId;
            UserDto user = restTemplate.getForObject(url, UserDto.class);
            log.debug("[IDENTITY-CLIENT] Retrieved user: userId={}, email={}", userId, user != null ? user.email() : null);
            return user;
        } catch (Exception e) {
            log.error("[IDENTITY-CLIENT] Failed to get user: userId={}, error={}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Lấy email của user
     */
    public String getUserEmail(Long userId) {
        UserDto user = getUserById(userId);
        return user != null ? user.email() : null;
    }

    public record UserDto(Long id, String email) {}
}
