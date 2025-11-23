package org.mobilehub.payment_service.exception;

public class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException() {
        super("Invalid webhook signature");
    }
}
