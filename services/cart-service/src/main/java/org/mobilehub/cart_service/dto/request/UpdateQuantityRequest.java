package org.mobilehub.cart_service.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuantityRequest {
    private int quantity;
}
