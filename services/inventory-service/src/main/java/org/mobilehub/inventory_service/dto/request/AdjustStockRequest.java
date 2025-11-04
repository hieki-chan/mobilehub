package org.mobilehub.inventory_service.dto.request;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class AdjustStockRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long delta;     // + nhập, - xuất
}