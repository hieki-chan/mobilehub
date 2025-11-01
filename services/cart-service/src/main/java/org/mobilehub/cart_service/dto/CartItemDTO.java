package org.mobilehub.cart_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long id;
    private Long productId;
    private int quantity;
    private BigDecimal subtotal;

    private ProductCartResponse product;
}
