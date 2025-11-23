package org.mobilehub.product_service.util;

import org.mobilehub.product_service.entity.ProductDiscount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DiscountUtils {

    public static BigDecimal applyDiscount(BigDecimal price, ProductDiscount productDiscount) {
        if(productDiscount == null)
            return price;
        return applyDiscount(price, productDiscount.getValueInPercent());
    }

    public static BigDecimal applyDiscount(BigDecimal price, Integer discountPercent) {
        if (price == null || discountPercent == null) {
            return price;
        }

        if (discountPercent <= 0) {
            return price;
        }

        if (discountPercent >= 100) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal discountAmount = price
                .multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }
}
