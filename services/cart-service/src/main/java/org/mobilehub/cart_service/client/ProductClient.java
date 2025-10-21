package org.mobilehub.cart_service.client;

import org.mobilehub.cart_service.dto.ProductCartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "product-service",
        url = "${product-service.url}"
)
public interface ProductClient {

    @GetMapping("/{id}/cart")
    ProductCartResponse getProductById(@PathVariable("id") Long id);
}
