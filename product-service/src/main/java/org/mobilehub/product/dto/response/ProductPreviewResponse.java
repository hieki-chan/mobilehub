package org.mobilehub.product.dto.response;

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
    String imageUrl;

    BigDecimal price;
    Integer discountInPercent;
    BigDecimal discountedPrice;
}
