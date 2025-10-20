package org.mobilehub.cart_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.cart_service.dto.CartAddRequest;
import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.dto.CartUpdateItemRequest;
import org.mobilehub.cart_service.service.CartService;
import org.mobilehub.shared.common.dto.ApiResponse; // ✅ dùng chung response format
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    /**
     * ✅ Lấy giỏ hàng của user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestParam Long userId) {
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(
                ApiResponse.<CartDTO>builder()
                        .code(1000)
                        .message("Get cart successfully")
                        .result(cart)
                        .build()
        );
    }

    /**
     * ✅ Thêm sản phẩm vào giỏ hàng (tự động lấy thông tin từ Product Service)
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(
            @RequestParam Long userId,
            @Valid @RequestBody CartAddRequest request) {

        CartDTO updatedCart = cartService.addItemToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CartDTO>builder()
                        .code(1000)
                        .message("Item added to cart successfully")
                        .result(updatedCart)
                        .build()
        );
    }

    /**
     * ✅ Cập nhật số lượng sản phẩm
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateItem(@Valid @RequestBody CartUpdateItemRequest request) {
        CartItemDTO updatedItem = cartService.updateItemQuantity(request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(
                ApiResponse.<CartItemDTO>builder()
                        .code(1000)
                        .message("Item quantity updated successfully")
                        .result(updatedItem)
                        .build()
        );
    }

    /**
     * ✅ Xóa 1 sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @RequestParam Long userId,
            @PathVariable Long itemId) {

        cartService.removeItem(userId, itemId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(1000)
                        .message("Item removed successfully")
                        .build()
        );
    }

    /**
     * ✅ Xóa toàn bộ sản phẩm trong giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(1000)
                        .message("Cart cleared successfully")
                        .build()
        );
    }

    /**
     * ✅ Lấy tổng tiền giỏ hàng
     */
    @GetMapping("/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotal(@RequestParam Long userId) {
        BigDecimal total = cartService.getTotal(userId);
        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .code(1000)
                        .message("Get total successfully")
                        .result(total)
                        .build()
        );
    }
}
