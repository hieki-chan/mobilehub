package org.mobilehub.order_service.exception;

public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String status) {
        super("Order cannot be cancelled in status: " + status);
    }
}
