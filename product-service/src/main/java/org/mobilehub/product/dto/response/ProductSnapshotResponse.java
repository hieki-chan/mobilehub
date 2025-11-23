package org.mobilehub.product.dto.response;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.mobilehub.product.entity.ProductStatus;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductSnapshotResponse {
    Long productId;
    Long variantId;
    // info
    BigDecimal price;
    BigDecimal discountedPrice;

    // product snapshot
    String productName;
    String productVariant;
    String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    ProductStatus status;
}
