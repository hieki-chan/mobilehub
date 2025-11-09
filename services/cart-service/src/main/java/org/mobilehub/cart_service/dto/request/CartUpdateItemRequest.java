package org.mobilehub.cart_service.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartUpdateItemRequest {
    private Long itemId;
    private int quantity;
}
