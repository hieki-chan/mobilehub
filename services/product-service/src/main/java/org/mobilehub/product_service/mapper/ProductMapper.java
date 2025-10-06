package org.mobilehub.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mobilehub.product_service.dto.request.CreateProductRequest;
import org.mobilehub.product_service.dto.request.UpdateProductRequest;
import org.mobilehub.product_service.dto.response.ProductDetailResponse;
import org.mobilehub.product_service.dto.response.ProductPreviewResponse;
import org.mobilehub.product_service.dto.response.ProductResponse;
import org.mobilehub.product_service.entity.Product;
import org.mobilehub.product_service.entity.ProductDiscount;
import org.mobilehub.product_service.entity.ProductImage;
import org.mobilehub.shared_contracts.media.ImageUploadedEvent;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);

    ProductResponse toProductResponse(Product product);

    @Mapping(target = "discount", source = "discount", qualifiedByName = "mapDiscountToInteger")

    ProductPreviewResponse toProductPreviewResponse(Product product);

    ProductDetailResponse toProductDetailResponse(Product product);

    Product updateProduct(@MappingTarget Product product, UpdateProductRequest updateRequest);

    @Named("mapDiscountToInteger")
    default Integer mapDiscountToInteger(ProductDiscount discount) {
        return discount != null ? discount.getValueInPercent() : 0;
    }

    // EVENT MAPPING
    ProductImage toProductImage(ImageUploadedEvent imageUploadedEvent);
}
