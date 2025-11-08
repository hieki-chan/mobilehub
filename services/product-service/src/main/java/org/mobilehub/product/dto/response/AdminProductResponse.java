package org.mobilehub.product.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.mobilehub.product.entity.ProductStatus;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminProductResponse {
    Long id;
    String name;
    ProductStatus status;

    Integer discountInPercent;

    ProductVariantResponse defaultVariant;
}
