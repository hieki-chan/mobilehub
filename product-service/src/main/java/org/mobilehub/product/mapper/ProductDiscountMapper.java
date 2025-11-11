package org.mobilehub.product.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.product.dto.request.CreateDiscountRequest;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.response.DiscountResponse;
import org.mobilehub.product.entity.ProductDiscount;

@Mapper(componentModel = "spring")
public interface ProductDiscountMapper {
    ProductDiscount toProductDiscount(CreateDiscountRequest request);
    DiscountResponse toDiscountResponse(ProductDiscount productDiscount);
}
