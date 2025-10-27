package org.mobilehub.inventory_service.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class InventoryReservationResponse {
    private String reservationId;
    private Long orderId;
    private String status;
    private Instant expiresAt;
    private List<InventoryReservationItemResponse> items;
}