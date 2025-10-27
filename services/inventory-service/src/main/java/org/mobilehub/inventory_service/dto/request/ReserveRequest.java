package org.mobilehub.inventory_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ReserveRequest {
    @NotNull
    private Long orderId;
    @NotBlank
    private String idempotencyKey;
    @NotNull private List<Item> items;

    @Data
    public static class Item {
        @NotNull private Long productId;
        @NotNull private Long quantity;
    }
}