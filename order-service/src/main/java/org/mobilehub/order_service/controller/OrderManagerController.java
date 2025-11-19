package org.mobilehub.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.entity.PaymentMethod;
import org.mobilehub.order_service.entity.ShippingMethod;
import org.mobilehub.order_service.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
@SuppressWarnings("unused")
public class OrderManagerController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrdersPaged(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) ShippingMethod shippingMethod
    ) {
        return ResponseEntity.ok(orderService.getOrdersPaged(page, size,  orderStatus, paymentMethod, shippingMethod));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId)
    {
        return ResponseEntity.ok(orderService.confirmOrder(orderId));
    }

    @PostMapping("/{orderId}/delivered")
    public ResponseEntity<?> setOrderDelivered(@PathVariable Long orderId)
    {
        return ResponseEntity.ok(orderService.setOrderDelivered(orderId));
    }

    @PostMapping("/{orderId}/failed")
    public ResponseEntity<?> setOrderFailed(@PathVariable Long orderId)
    {
        return ResponseEntity.ok(orderService.setOrderFailed(orderId));
    }
}
