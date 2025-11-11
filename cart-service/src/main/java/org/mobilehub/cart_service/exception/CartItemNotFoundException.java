package org.mobilehub.cart_service.exception;


public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException(Long itemId) {
        super("Cart item not found with ID: " + itemId);
    }
}
