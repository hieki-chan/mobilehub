package org.mobilehub.rating_service.client;

import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;


//@FeignClient(name = "product-service", url = "${service.product}")
public interface ProductClient {

    //@GetMapping("/products/carts")
    //List<ProductCartResponse> getProductsInCart(@RequestParam List<Long> productIds);
}
