package org.mobilehub.product_service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String description;

    Integer discountInPercent;
    Integer sold;

    ProductVariantResponse defaultVariant;
}
