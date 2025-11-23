package org.mobilehub.inventory_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mobilehub.inventory_service.dto.request.AdjustStockRequest;
import org.mobilehub.inventory_service.dto.response.InventoryReservationResponse;
import org.mobilehub.inventory_service.dto.response.InventoryStockResponse;
import org.mobilehub.inventory_service.dto.request.ReserveRequest;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API cho inventory:
 * - Xem và điều chỉnh tồn kho
 * - Reserve / Commit / Release
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // ===== STOCK =====

    /** GET /api/inventory/stock/{productId} */
    @GetMapping("/stock/{productId}")
    public ResponseEntity<InventoryStockResponse> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getStock(productId));
    }

    /** POST /api/inventory/stock/adjust  (body: productId, delta) */
    @PostMapping("/stock/adjust")
    public ResponseEntity<InventoryStockResponse> adjustStock(@RequestBody @Valid AdjustStockRequest req) {
        return ResponseEntity.ok(inventoryService.adjustStock(req.getProductId(), req.getDelta()));
    }

    // ===== RESERVATIONS =====

    /** POST /api/inventory/reservations  (reserve hàng khi order.created) */
    @PostMapping("/reservations")
    public ResponseEntity<InventoryReservationResponse> reserve(@RequestBody @Valid ReserveRequest req) {
        // map DTO -> entity stub để dùng với service hiện tại
        List<InventoryReservationItem> items = req.getItems().stream().map(i -> {
            InventoryReservationItem it = new InventoryReservationItem();
            it.setProductId(i.getProductId());
            it.setQuantity(i.getQuantity());
            return it;
        }).toList();

        var res = inventoryService.reserve(req.getOrderId(), items, req.getIdempotencyKey());
        return ResponseEntity.ok(res);
    }

    /** POST /api/inventory/reservations/{reservationId}/commit  (payment succeeded) */
    @PostMapping("/reservations/{reservationId}/commit")
    public ResponseEntity<InventoryReservationResponse> commit(@PathVariable String reservationId) {
        return ResponseEntity.ok(inventoryService.commit(reservationId));
    }

    /** POST /api/inventory/reservations/{reservationId}/release  (payment failed/canceled) */
    @PostMapping("/reservations/{reservationId}/release")
    public ResponseEntity<InventoryReservationResponse> release(@PathVariable String reservationId) {
        return ResponseEntity.ok(inventoryService.release(reservationId));
    }

    @PostMapping("/reservations/{reservationId}/cancel")
    public InventoryReservationResponse cancel(@PathVariable String reservationId) {
        return inventoryService.cancel(reservationId);
    }

}
