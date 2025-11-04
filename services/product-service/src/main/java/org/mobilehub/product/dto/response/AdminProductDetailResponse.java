package org.mobilehub.product.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminProductDetailResponse {
    Long id;
    String name;
    String description;
    String mainImageUrl;
    List<String> otherImageUrls;

    ProductSpecResponse spec;
    BigDecimal price;

    DiscountResponse discount;
}
