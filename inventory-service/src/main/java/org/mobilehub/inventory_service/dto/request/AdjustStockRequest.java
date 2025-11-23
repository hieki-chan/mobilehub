package org.mobilehub.inventory_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class AdjustStockRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long delta;     // + nhập, - xuất
}