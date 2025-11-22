package org.mobilehub.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service", url = "${services.inventory}")
public interface InventoryClient {

    @PostMapping("/api/inventory/reservations/{reservationId}/commit")
    void commit(@PathVariable String reservationId);

    @PostMapping("/api/inventory/reservations/{reservationId}/release")
    void release(@PathVariable String reservationId);
}
