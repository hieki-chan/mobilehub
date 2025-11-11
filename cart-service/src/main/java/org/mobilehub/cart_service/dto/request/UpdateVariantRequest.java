package org.mobilehub.cart_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateVariantRequest {
    @NotNull
    Long variantId;
}
