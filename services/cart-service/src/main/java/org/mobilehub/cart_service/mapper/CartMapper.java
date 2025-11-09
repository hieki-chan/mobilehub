package org.mobilehub.cart_service.mapper;

import org.mapstruct.*;
import org.mobilehub.cart_service.dto.request.CartAddRequest;
import org.mobilehub.cart_service.dto.response.CartItemResponseDTO;
import org.mobilehub.cart_service.dto.response.CartResponseDTO;
import org.mobilehub.cart_service.dto.response.ProductCartResponse;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartItemResponseDTO toCartItemDTO(CartItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    CartItem toCartItem(CartAddRequest request);

    @Mapping(target = "items", expression = "java(toCartItemDTOList(entity.getItems()))")
    CartResponseDTO toCartDTO(Cart entity);

    List<CartItemResponseDTO> toCartItemDTOList(List<CartItem> items);

    CartItemResponseDTO toCartItemResponseDTO(ProductCartResponse response);
}
