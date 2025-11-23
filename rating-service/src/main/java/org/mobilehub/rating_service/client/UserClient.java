package org.mobilehub.rating_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${service.identity}")
public interface UserClient {

    @GetMapping("/users/{userId}/username")
    String getUserName(@PathVariable Long userId);
}
