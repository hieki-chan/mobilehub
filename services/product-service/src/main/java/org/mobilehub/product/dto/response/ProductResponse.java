package org.mobilehub.product.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
    private String name;

    private BigDecimal price;
    private Integer discountInPercent;
    private BigDecimal discountedPrice;
}
