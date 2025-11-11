package org.mobilehub.product.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.CreateProductSpecRequest;
import org.mobilehub.product.entity.ProductSpec;

@Mapper(componentModel = "spring")
public interface ProductSpecMapper {
    ProductSpec toProductSpec(CreateProductSpecRequest request);
}
