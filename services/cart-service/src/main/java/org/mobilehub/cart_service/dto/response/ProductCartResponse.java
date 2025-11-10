package org.mobilehub.cart_service.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    String name;
    Integer discountInPercent;

    List<VariantCartResponse> variants;

    public BigDecimal getPrice(Long variantId)
    {
        return new BigDecimal(0);
    }
}

