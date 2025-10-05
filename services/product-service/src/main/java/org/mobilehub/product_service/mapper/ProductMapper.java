package org.mobilehub.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mobilehub.product_service.dto.request.CreateProductRequest;
import org.mobilehub.product_service.dto.request.UpdateProductRequest;
import org.mobilehub.product_service.dto.response.ProductResponse;
import org.mobilehub.product_service.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);
    ProductResponse toProductResponse(Product product);

    Product updateProduct(@MappingTarget Product product, UpdateProductRequest updateRequest);
}
