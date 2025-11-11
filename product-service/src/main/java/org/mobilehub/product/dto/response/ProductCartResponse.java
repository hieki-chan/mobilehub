package org.mobilehub.product.dto.response;

import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.mobilehub.product.entity.ProductStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    String name;
    Integer discountInPercent;

    List<ProductVariantResponse> variants;

    @Enumerated(EnumType.STRING)
    ProductStatus status;
}
