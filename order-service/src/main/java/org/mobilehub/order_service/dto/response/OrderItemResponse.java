package org.mobilehub.order_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long productId;
    String productName;
    String productVariant;
    String thumbnailUrl;
    BigDecimal originalPrice;
    BigDecimal finalPrice;
    int quantity;
}
