package org.mobilehub.product_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.mobilehub.product_service.entity.ProductStatus;

import java.util.List;

@Getter
@Setter
public class CreateProductRequest {
    @NotBlank()
    String name;

    @NotBlank()
    String description;

    @NotNull()
    ProductStatus status =  ProductStatus.ACTIVE;

    // discount
    @Valid
    CreateDiscountRequest discount;

    // spec
    @Valid
    CreateProductSpecRequest spec;

    @Valid
    CreateProductVariantRequest[] variants;

    @Valid
    Integer defaultVariantIndex;

    List<List<Integer>> imageMap;
}
