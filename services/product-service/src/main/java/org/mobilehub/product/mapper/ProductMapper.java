package org.mobilehub.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.UpdateProductRequest;
import org.mobilehub.product.dto.response.*;
import org.mobilehub.product.entity.Product;
import org.mobilehub.product.entity.ProductDiscount;
import org.mobilehub.product.entity.ProductImage;
import org.mobilehub.shared.contracts.media.ImageUploadedEvent;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);

    // region ADMIN RESPONSES
    @Mapping(target = "discountInPercent", source = "discount", qualifiedByName = "mapDiscountToInteger")
    AdminProductResponse toAdminProductResponse(Product product);

    //@Mapping(target = "discountInPercent", source = "discount", qualifiedByName = "mapDiscountToInteger")
    AdminProductDetailResponse toAdminProductDetailResponse(Product product);

    // endregion
    @Mapping(target = "discountInPercent", source = "discount", qualifiedByName = "mapDiscountToInteger")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "discountInPercent", source = "discount", qualifiedByName = "mapDiscountToInteger")
    ProductPreviewResponse toProductPreviewResponse(Product product);

    @Mapping(target = "discountInPercent", source = "discount", qualifiedByName = "mapDiscountToInteger")
    ProductDetailResponse toProductDetailResponse(Product product);

    @Mapping(target = "discountInPercent", source = "discount", qualifiedByName = "mapDiscountToInteger")
    ProductCartResponse toProductCartResponse(Product product);

    void updateProduct(@MappingTarget Product product, UpdateProductRequest updateRequest);

    @Named("mapDiscountToInteger")
    default Integer mapDiscountToInteger(ProductDiscount discount) {
        return discount != null ? discount.getValueInPercent() : 0;
    }

    default Product mapProduct(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
