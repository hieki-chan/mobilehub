package org.mobilehub.inventory_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.inventory_service.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/reservations")
@RequiredArgsConstructor
public class InventoryReservationController {

    private final InventoryService inventoryService;

    @PostMapping("/{reservationId}/commit")
    public ResponseEntity<?> commit(@PathVariable String reservationId) {
        return ResponseEntity.ok(inventoryService.commit(reservationId));
    }

    @PostMapping("/{reservationId}/release")
    public ResponseEntity<?> release(@PathVariable String reservationId) {
        return ResponseEntity.ok(inventoryService.release(reservationId));
    }
}
