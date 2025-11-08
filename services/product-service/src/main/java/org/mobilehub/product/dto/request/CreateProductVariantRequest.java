package org.mobilehub.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductVariantRequest {
    @Size(max = 255)
    private String color_label;
    @Size(max = 7)
    private String color_hex;

    private Integer storage_cap;
    private Integer ram;

    @NotNull
    private BigDecimal price;
}