package org.mobilehub.inventory_service.dto.response;

import lombok.Data;

@Data
public class InventoryReservationItemResponse {
    private Long productId;
    private Long quantity;
}