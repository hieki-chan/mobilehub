package org.mobilehub.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "identity-service",
        url = "${services.identity.base-url:http://localhost:8081}"
)
public interface UserClient {

    @GetMapping("/internal/users/{userId}")
    UserDto getUser(@PathVariable Long userId);

    record UserDto(Long id, String email) {}
}
