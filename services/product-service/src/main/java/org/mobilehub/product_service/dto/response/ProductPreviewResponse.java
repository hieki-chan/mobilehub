package org.mobilehub.product_service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPreviewResponse {
    String name;
    BigDecimal price;
    Integer discount;
    BigDecimal discountedPrice;
}
