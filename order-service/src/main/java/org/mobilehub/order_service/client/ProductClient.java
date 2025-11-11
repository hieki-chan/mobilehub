package org.mobilehub.order_service.client;

import org.mobilehub.order_service.dto.request.ProductSnapshotRequest;
import org.mobilehub.order_service.dto.response.ProductSnapshotResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "${service.product}")
public interface ProductClient {

    @GetMapping("/products/{productId}/validate")
    Boolean checkProductVariantValid(
            @PathVariable("productId") Long productId,
            @RequestParam Long variantId);

    @PostMapping("/products/snapshots")
    List<ProductSnapshotResponse> getProductsSnapshot(
            @RequestBody List<ProductSnapshotRequest> requests);
}
