package org.mobilehub.cart_service.mapper;

import org.mapstruct.*;
import org.mobilehub.cart_service.dto.*;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // ✅ Entity → DTO: CartItem
    @Mapping(target = "subtotal",
            expression = "java(calculateSubtotal(entity))")
    @Mapping(target = "product", ignore = true) // sẽ set trong CartService sau
    CartItemDTO toCartItemDTO(CartItem entity);

    // ✅ DTO → Entity: CartItem (từ request)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    CartItem toCartItem(CartAddRequest request);

    // ✅ Entity → DTO: Cart
    @Mapping(target = "totalAmount",
            expression = "java(calculateTotal(entity))")
    @Mapping(target = "items", expression = "java(toCartItemDTOList(entity.getItems()))")
    CartDTO toCartDTO(Cart entity);

    // ✅ List<CartItem> → List<CartItemDTO>
    List<CartItemDTO> toCartItemDTOList(List<CartItem> items);

    // ==============================
    // 🧮 Helper methods
    // ==============================

    default BigDecimal calculateSubtotal(CartItem entity) {
        if (entity == null || entity.getPrice() == null || entity.getQuantity() == 0) {
            return BigDecimal.ZERO;
        }
        return entity.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
    }

    default BigDecimal calculateTotal(Cart entity) {
        if (entity == null || entity.getItems() == null) {
            return BigDecimal.ZERO;
        }
        return entity.getItems().stream()
                .map(this::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
