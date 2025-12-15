package org.mobilehub.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.entity.InstallmentOrderMapping;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.repository.InstallmentOrderMappingRepository;
import org.mobilehub.order_service.repository.OrderRepository;
import org.mobilehub.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderRepository orderRepo;
    private final OrderService orderService;
    private final InstallmentOrderMappingRepository mappingRepo;

    @GetMapping("/{orderId}/reservation")
    public ResponseEntity<?> getReservation(@PathVariable Long orderId) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found " + orderId));

        return ResponseEntity.ok(new ReservationDto(order.getId(), order.getReservationId()));
    }

    public record ReservationDto(Long orderId, String reservationId) {}

    @GetMapping("/{orderId}")
    public OrderResponse getById(@PathVariable Long orderId) {
        return orderService.getById(orderId);
    }

    @GetMapping("/by-application/{applicationId}")
    public ResponseEntity<OrderBasicDto> getByApplicationId(@PathVariable Long applicationId) {
        InstallmentOrderMapping mapping = mappingRepo.findByApplicationId(applicationId)
                .orElseThrow(() -> new IllegalStateException("Order not found for application " + applicationId));
        
        Order order = orderRepo.findById(mapping.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found " + mapping.getOrderId()));
        
        return ResponseEntity.ok(new OrderBasicDto(order.getId(), order.getUserId()));
    }

    public record OrderBasicDto(Long id, Long userId) {}

}
