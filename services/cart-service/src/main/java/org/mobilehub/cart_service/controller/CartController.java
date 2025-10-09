package org.mobilehub.cart_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.cart_service.dto.CartAddRequest;
import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.dto.CartUpdateItemRequest;
import org.mobilehub.cart_service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;


    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam Long userId) {
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }


    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam Long userId,
            @Valid @RequestBody CartAddRequest request) {
        CartDTO updatedCart = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }


    @PutMapping("/update")
    public ResponseEntity<CartItemDTO> updateItem(@Valid @RequestBody CartUpdateItemRequest request) {
        CartItemDTO updatedItem = cartService.updateItemQuantity(request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String, String>> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item removed successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotal(@RequestParam Long userId) {
        BigDecimal total = cartService.getTotal(userId);
        return ResponseEntity.ok(total);
    }
}
