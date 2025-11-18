package org.mobilehub.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId.toString());
    }

    public OrderNotFoundException(String userId) {
        super("Order not found with user ID: " + userId);
    }
}
