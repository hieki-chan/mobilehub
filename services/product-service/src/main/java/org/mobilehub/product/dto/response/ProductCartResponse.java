package org.mobilehub.product.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    String name;
    Integer discountInPercent;

    List<ProductVariantResponse> variants;
}
