package org.mobilehub.payment_service.entity;

public enum PaymentStatus {
    NEW,
    REQUIRES_ACTION,
    AUTHORIZED,
    CAPTURED,
    PARTIALLY_CAPTURED,
    FAILED,
    CANCELED,
    REFUNDED,
    PARTIALLY_REFUNDED;

    public boolean isFinal() {
        return this == CAPTURED || this == FAILED || this == CANCELED || this == REFUNDED;
    }
}
