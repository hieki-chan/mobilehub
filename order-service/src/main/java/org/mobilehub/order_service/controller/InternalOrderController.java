package org.mobilehub.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderRepository orderRepo;

    @GetMapping("/{orderId}/reservation")
    public ResponseEntity<?> getReservation(@PathVariable Long orderId) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found " + orderId));

        return ResponseEntity.ok(new ReservationDto(order.getId(), order.getReservationId()));
    }

    public record ReservationDto(Long orderId, String reservationId) {}
}
