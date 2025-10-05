package org.mobilehub.product_service.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.product_service.dto.request.CreateDiscountRequest;
import org.mobilehub.product_service.dto.response.DiscountResponse;
import org.mobilehub.product_service.entity.ProductDiscount;

@Mapper(componentModel = "spring")
public interface ProductDiscountMapper {
    ProductDiscount toProductDiscount(CreateDiscountRequest request);
    DiscountResponse toDiscountResponse(ProductDiscount productDiscount);
}
