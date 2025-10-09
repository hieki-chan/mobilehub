package org.mobilehub.cart_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    // ✅ Dùng fully-qualified name để MapStruct hiểu đúng
    @Mapping(target = "subtotal",
            expression = "java(entity.getPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity())))")
    CartItemDTO toCartItemDTO(CartItem entity);

    @Mapping(target = "totalAmount",
            expression = "java(calculateTotal(entity))")
    @Mapping(target = "items", source = "items")
    CartDTO toCartDTO(Cart entity);

    List<CartItemDTO> toCartItemDTOList(List<CartItem> items);

    // ✅ Hàm helper tự động được gọi trong mapping
    default BigDecimal calculateTotal(Cart entity) {
        if (entity == null || entity.getItems() == null) {
            return BigDecimal.ZERO;
        }
        return entity.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
