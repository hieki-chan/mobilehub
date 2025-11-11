package org.mobilehub.order_service.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String thumbnailUrl;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
}
