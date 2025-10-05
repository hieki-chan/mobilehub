package org.mobilehub.cart_service.controller;

import jdk.jfr.Frequency;
import org.mobilehub.cart_service.dto.CartAddRequest;
import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.dto.CartUpdateItemRequest;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;
import org.mobilehub.cart_service.mapper.CartMapper;
import org.mobilehub.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class CartController {
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam(required = false) Long userId){
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(CartMapper.toCartDTO(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam(required = false) Long userId,
            @RequestBody CartAddRequest request){
        Cart cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(CartMapper.toCartDTO(cart));
    }

    @PutMapping("/update")
    public ResponseEntity<CartItemDTO> updateItem(@RequestBody CartUpdateItemRequest request){
        CartItem item = cartService.updateItemQuantity(request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(CartMapper.toCartItemDTO(item));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId){
        try{
            cartService.removeItem(itemId);
            return ResponseEntity.ok("Item remove successfully");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestParam Long userId){
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("Cart cleared successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotal(@RequestParam Long userId){
        try {
            BigDecimal total = cartService.getTotal(userId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
