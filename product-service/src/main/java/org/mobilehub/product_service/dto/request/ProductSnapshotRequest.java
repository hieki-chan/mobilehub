package org.mobilehub.product_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSnapshotRequest {
    Long productId;
    Long variantId;
}
