package org.mobilehub.product.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {
    String color_label;
    String color_hex;
    String storage_cap;
    String ram;
    BigDecimal price;
    String imageUrl;
}
