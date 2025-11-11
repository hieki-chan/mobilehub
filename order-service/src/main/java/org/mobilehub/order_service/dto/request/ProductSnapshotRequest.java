package org.mobilehub.order_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSnapshotRequest {
    Long productId;
    Long variantId;
}
