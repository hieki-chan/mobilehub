package org.mobilehub.cart_service.dto.response;


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
    Integer discountInPercent;
    BigDecimal discountedPrice;

    public BigDecimal getPrice(Long variantId)
    {
        return new BigDecimal(0);
    }
}

