package org.mobilehub.product_service.entity;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE(0),
    INACTIVE(1),
    COMING_SOON(2);

    private final int code;

    ProductStatus(int code) {
        this.code = code;
    }

    public static ProductStatus fromCode(int code) {
        for (ProductStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Invalid ProductStatus code: " + code);
    }
}