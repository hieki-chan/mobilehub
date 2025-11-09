package org.mobilehub.cart_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponseDTO {
    Long id;
    Long productId;
    int quantity;

    // ==product info
    String name;

    BigDecimal subtotal;
}
