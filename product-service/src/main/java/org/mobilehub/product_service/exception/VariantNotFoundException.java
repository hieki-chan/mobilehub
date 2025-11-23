package org.mobilehub.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VariantNotFoundException extends RuntimeException {
    public VariantNotFoundException(Long productId, Long variantId) {
        super(String.format("Could not find variant with ID %d for product ID %d", variantId, productId));
    }

    public VariantNotFoundException(String message) {
        super(message);
    }

    public VariantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}