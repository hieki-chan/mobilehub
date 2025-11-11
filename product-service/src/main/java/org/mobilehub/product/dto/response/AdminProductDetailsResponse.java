package org.mobilehub.product.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.mobilehub.product.entity.ProductStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminProductDetailsResponse {
    Long id;
    String name;
    String description;
    ProductStatus status;

    ProductSpecResponse spec;

    DiscountResponse discount;

    List<AdminProductVariantResponse> variants;

    Long defaultVariantId;
}
