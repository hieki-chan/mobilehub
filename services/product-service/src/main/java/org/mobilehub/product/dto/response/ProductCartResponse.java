package org.mobilehub.product.dto.response;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter @Setter @FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    String name;
    String material;
    String imageUrl;
    BigDecimal price;
    Integer discountInPercent;
    BigDecimal discountedPrice;
}
