package org.mobilehub.order_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderStatusUpdater {

    private final OrderRepository orderRepo;

    private static boolean isTerminal(OrderStatus s) {
        return s == OrderStatus.DELIVERED || s == OrderStatus.CANCELLED || s == OrderStatus.FAILED;
    }

    /** inventory.reserved → giữ hàng xong, chờ thanh toán */
    @Transactional
    public void markAwaitingPayment(Long orderId, String reservationId, Instant expiresAt) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        // Không lùi trạng thái nếu đã sang các bước sau/terminal
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.SHIPPING
                || order.getStatus() == OrderStatus.DELIVERED
                || isTerminal(order.getStatus())) {
            return;
        }

        // Idempotent: đã PENDING với cùng reservationId thì bỏ qua
        if (order.getStatus() == OrderStatus.PENDING
                && reservationId != null
                && reservationId.equals(order.getReservationId())) {
            return;
        }

        order.setStatus(OrderStatus.PENDING);
        order.setReservationId(reservationId);
        order.setReservedUntil(expiresAt);
        order.setCancelReason(null);
    }

    /** inventory.committed → thanh toán thành công */
    @Transactional
    public void markPaid(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.PAID) return;        // idempotent
        if (isTerminal(order.getStatus())) return;                 // không lùi từ CANCELLED/FAILED/DELIVERED
        if (order.getStatus() == OrderStatus.SHIPPING
                || order.getStatus() == OrderStatus.DELIVERED) return;

        order.setStatus(OrderStatus.PAID);
        order.setCancelReason(null);
        // Tuỳ bạn: có thể clear hold vì đã commit xong
        // order.setReservationId(null);
        // order.setReservedUntil(null);
    }

    /** fulfillment/shipper gọi: PAID → SHIPPED */
    @Transactional
    public void markShipped(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED) return;
        if (isTerminal(order.getStatus())) return;
        // Chỉ cho phép ship khi đã PAID
        if (order.getStatus() != OrderStatus.PAID) return;

        order.setStatus(OrderStatus.SHIPPING);
    }

    /** hoàn tất giao hàng */
    @Transactional
    public void markDelivered(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) return;
        if (isTerminal(order.getStatus())) return;
        // thường: chỉ DELIVERED khi đang SHIPPED
        if (order.getStatus() != OrderStatus.SHIPPING) return;

        order.setStatus(OrderStatus.DELIVERED);
    }

    /** inventory.released → huỷ do thanh toán fail/timeout (từ trạng thái PENDING) */
    @Transactional
    public void markReleased(Long orderId, String reason) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) return; // idempotent
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.SHIPPING
                || order.getStatus() == OrderStatus.DELIVERED) return;

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason != null ? reason : "RELEASED");
        // order.setReservationId(null);
        // order.setReservedUntil(null);
    }

    /** inventory.rejected → hết hàng */
    @Transactional
    public void markRejected(Long orderId, String reason) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.FAILED) return; // idempotent
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.SHIPPING
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) return;

        order.setStatus(OrderStatus.FAILED);
        order.setCancelReason(reason != null ? reason : "OUT_OF_STOCK");
        // order.setReservationId(null);
        // order.setReservedUntil(null);
    }
}
