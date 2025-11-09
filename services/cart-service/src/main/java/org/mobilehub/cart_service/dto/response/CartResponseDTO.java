package org.mobilehub.cart_service.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {
    private List<CartItemResponseDTO> items;

    private BigDecimal totalPrice;
}
