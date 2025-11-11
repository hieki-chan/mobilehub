package org.mobilehub.cart_service.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuantityRequest {
    @Min(1)
    private int quantity;
}
