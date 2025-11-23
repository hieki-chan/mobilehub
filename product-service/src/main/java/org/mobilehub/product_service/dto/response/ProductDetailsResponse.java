package org.mobilehub.product_service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailsResponse {
    Long id;
    String name;
    String description;

    ProductSpecResponse spec;

    DiscountResponse discount;

    List<AdminProductVariantResponse> variants;

    Long defaultVariantId;
    Integer sold;
}
