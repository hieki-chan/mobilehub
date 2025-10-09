package org.mobilehub.order_service.controller;

import org.mobilehub.order_service.dto.request.OrderCancelRequest;
import org.mobilehub.order_service.dto.request.OrderCreateRequest;
import org.mobilehub.order_service.dto.request.OrderUpdateStatusRequest;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.dto.response.OrderSummaryResponse;
import org.mobilehub.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 🟢 Lấy chi tiết 1 đơn hàng theo ID
     * Endpoint: GET /orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 🟢 Lấy danh sách đơn hàng của 1 user
     * Endpoint: GET /orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByUser(@PathVariable Long userId) {
        List<OrderSummaryResponse> response = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 🟡 Cập nhật trạng thái đơn hàng (Admin hoặc hệ thống)
     * Endpoint: PUT /orders/{id}/status
     * Body: { "status": "SHIPPED" }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderUpdateStatusRequest request
    ) {
        OrderResponse response = orderService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 🔴 Hủy đơn hàng (chỉ khi trạng thái là PENDING)
     * Endpoint: PUT /orders/{id}/cancel
     * Body: { "reason": "Tôi đổi ý" }
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestBody OrderCancelRequest request
    ) {
        OrderResponse response = orderService.cancelOrder(id, request);
        return ResponseEntity.ok(response);
    }
}
