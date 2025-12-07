package org.mobilehub.product_service.dto.response;

import lombok.Data;

@Data
public class InventoryStockResponse {
    private Long productId;
    private Long onHand;
    private Long reserved;
    private Long available;
}