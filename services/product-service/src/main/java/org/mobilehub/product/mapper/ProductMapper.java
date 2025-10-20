package org.mobilehub.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.UpdateProductRequest;
import org.mobilehub.product.dto.response.ProductCartResponse;
import org.mobilehub.product.dto.response.ProductDetailResponse;
import org.mobilehub.product.dto.response.ProductPreviewResponse;
import org.mobilehub.product.dto.response.ProductResponse;
import org.mobilehub.product.entity.Product;
import org.mobilehub.product.entity.ProductDiscount;
import org.mobilehub.product.entity.ProductImage;
import org.mobilehub.shared.contracts.media.ImageUploadedEvent;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);

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

    // EVENT MAPPING
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "publicId", source = "publicId")
    @Mapping(target = "isMain", constant = "false")
    ProductImage toProductImage(ImageUploadedEvent event);

    default Product mapProduct(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
