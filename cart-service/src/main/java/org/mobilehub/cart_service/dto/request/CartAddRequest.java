package org.mobilehub.cart_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAddRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long variantId;
    @Min(1)
    private int quantity;
}
