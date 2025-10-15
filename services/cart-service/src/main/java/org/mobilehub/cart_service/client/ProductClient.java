package org.mobilehub.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8080")
public interface ProductClient {

    //@GetMapping("/api/products/{id}")
    //ProductResponse getProduct(@PathVariable("id") Long productId);
}
