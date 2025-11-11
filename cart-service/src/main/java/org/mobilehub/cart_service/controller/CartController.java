package org.mobilehub.cart_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.cart_service.dto.request.CartAddRequest;
import org.mobilehub.cart_service.dto.request.UpdateVariantRequest;
import org.mobilehub.cart_service.dto.response.CartResponseDTO;
import org.mobilehub.cart_service.dto.request.UpdateQuantityRequest;
import org.mobilehub.cart_service.dto.response.UpdateQuantityResponse;
import org.mobilehub.cart_service.dto.response.UpdateVariantResponse;
import org.mobilehub.cart_service.service.CartService;
import org.mobilehub.shared.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Validated
@SuppressWarnings("unused")
public class CartController {

    private final CartService cartService;

    /**
     * Lấy giỏ hàng của user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(@PathVariable Long userId) {
        CartResponseDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(
                ApiResponse.<CartResponseDTO>builder()
                        .code(1000)
                        .message("Get cart successfully")
                        .result(cart)
                        .build()
        );
    }

    /**
     * Thêm sản phẩm vào giỏ hàng (tự động lấy thông tin từ Product Service)
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartAddRequest request) {

        CartResponseDTO updatedCart = cartService.addItemToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CartResponseDTO>builder()
                        .code(1000)
                        .message("Item added to cart successfully")
                        .result(updatedCart)
                        .build()
        );
    }

    /**
     * Cập nhật số lượng sản phẩm
     */
    @PutMapping("/{userId}/items/{itemId}/quantity")
    public ResponseEntity<ApiResponse<UpdateQuantityResponse>> updateItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        var updatedItem = cartService.updateItemQuantity(userId, itemId, request.getQuantity());
        return ResponseEntity.ok(
                ApiResponse.<UpdateQuantityResponse>builder()
                        .code(1000)
                        .message("Item quantity updated successfully")
                        .result(updatedItem)
                        .build()
        );
    }

    @PutMapping("/{userId}/items/{itemId}/variant")
    public ResponseEntity<ApiResponse<UpdateVariantResponse>> updateVariant(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateVariantRequest request) {
        var updatedItem = cartService.updateItemVariant(userId, itemId, request.getVariantId());
        return ResponseEntity.ok(
                ApiResponse.<UpdateVariantResponse>builder()
                        .code(1000)
                        .message("Item quantity updated successfully")
                        .result(updatedItem)
                        .build()
        );
    }

    /**
     * Xóa 1 sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/items/{itemId}")
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
     * Xóa toàn bộ sản phẩm trong giỏ hàng
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
     * Lấy tổng tiền giỏ hàng
     */
//    @GetMapping("/total")
//    public ResponseEntity<ApiResponse<BigDecimal>> getTotal(@RequestParam Long userId) {
//        BigDecimal total = cartService.getTotal(userId);
//        return ResponseEntity.ok(
//                ApiResponse.<BigDecimal>builder()
//                        .code(1000)
//                        .message("Get total successfully")
//                        .result(total)
//                        .build()
//        );
//    }
}
