package org.mobilehub.product_service.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.product_service.dto.request.CreateProductSpecRequest;
import org.mobilehub.product_service.entity.ProductSpec;

@Mapper(componentModel = "spring")
public interface ProductSpecMapper {
    ProductSpec toProductSpec(CreateProductSpecRequest request);
}
