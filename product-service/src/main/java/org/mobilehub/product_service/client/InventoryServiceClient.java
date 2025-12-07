package org.mobilehub.product_service.client;

import org.mobilehub.product_service.config.FeignMultipartConfig;
import org.mobilehub.product_service.dto.response.InventoryStockResponse;
import org.mobilehub.shared.contracts.media.ImageUploadEvent;
import org.mobilehub.shared.contracts.media.MultipleImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "inventory-service",
        url = "${service.inventory.url}",
        configuration = FeignMultipartConfig.class
)
public interface InventoryServiceClient {
    @GetMapping("/inventory/stock/{productId}")
    InventoryStockResponse getStock(@PathVariable Long productId);
}
