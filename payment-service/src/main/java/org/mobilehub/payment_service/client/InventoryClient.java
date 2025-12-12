package org.mobilehub.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "inventory-service",
        url = "${services.inventory.base-url}"
)
public interface InventoryClient {

    @PostMapping("/inventory/reservations/{reservationId}/commit")
    void commit(@PathVariable String reservationId);

    @PostMapping("/inventory/reservations/{reservationId}/release")
    void release(@PathVariable String reservationId);

}
