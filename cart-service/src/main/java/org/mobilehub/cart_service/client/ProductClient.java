package org.mobilehub.cart_service.client;

import org.mobilehub.cart_service.dto.response.ProductCartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "product-service", url = "${service.product}")
public interface ProductClient {

    @GetMapping("/products/{productId}/validate")
    Boolean checkProductVariantValid(
            @PathVariable("productId") Long productId,
            @RequestParam Long variantId);

    @GetMapping("/products/carts")
    List<ProductCartResponse> getProductsInCart(@RequestParam List<Long> productIds);
}
