package org.mobilehub.cart_service.mapper;

import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {
    public static CartItemDTO toCartItemDTO(CartItem entity){
        if(entity == null) return null;
        return CartItemDTO.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .thumbnailUrl(entity.getThumbnailUrl())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .subtotal(entity.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity())))
                .build();
    }

    public static CartDTO toCartDTO(Cart entity){
        if(entity == null) return null;
        List<CartItemDTO> itemDTOS = entity.getItems() == null
                ?List.of()
                :entity.getItems().stream().map(CartMapper::toCartItemDTO).collect(Collectors.toList());
        BigDecimal total = itemDTOS.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .totalAmount(total)
                .items(itemDTOS)
                .build();
    }
}
