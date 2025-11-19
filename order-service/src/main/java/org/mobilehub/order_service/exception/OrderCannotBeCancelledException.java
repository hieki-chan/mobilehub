package org.mobilehub.order_service.exception;

public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String cause) {
        super("Order cannot be cancelled: " + cause);
    }
}
