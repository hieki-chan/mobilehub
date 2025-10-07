package org.mobilehub.product.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.mobilehub.product.entity.ProductStatus;

@Converter(autoApply = true)
public class ProductStatusConverter implements AttributeConverter<ProductStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProductStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public ProductStatus convertToEntityAttribute(Integer code) {
        return code != null ? ProductStatus.fromCode(code) : null;
    }
}

