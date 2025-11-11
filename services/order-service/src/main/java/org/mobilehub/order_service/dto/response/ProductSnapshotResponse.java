package org.mobilehub.order_service.dto.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSnapshotResponse {
    // info
    BigDecimal price;
    BigDecimal discountedPrice;

    // product snapshot
    String productName;
    String productVariant;
    String thumbnailUrl;
}
