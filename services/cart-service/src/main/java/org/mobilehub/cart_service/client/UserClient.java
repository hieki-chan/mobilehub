package org.mobilehub.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${gateway.url}")
public interface UserClient {

    @GetMapping("/api/v1/users/{userId}/exists")
    Boolean exists(@PathVariable("userId") Long userId);
}
