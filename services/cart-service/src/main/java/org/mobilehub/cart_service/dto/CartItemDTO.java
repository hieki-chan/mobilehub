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
    private String productName;
    private String thumbnailUrl;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
}
