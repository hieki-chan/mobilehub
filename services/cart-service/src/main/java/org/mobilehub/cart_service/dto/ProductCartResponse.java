package org.mobilehub.cart_service.dto;


import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter @Setter @FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    String name;
    String color;
    String imageUrl;
    BigDecimal price;
    Integer discountInPercent;
    BigDecimal discountedPrice;
}

