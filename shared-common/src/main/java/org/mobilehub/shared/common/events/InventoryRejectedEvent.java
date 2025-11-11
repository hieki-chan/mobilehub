package org.mobilehub.shared.common.events;

import java.util.List;

public record InventoryRejectedEvent(
        String eventId,
        Long orderId,
        String reason,
        List<Missing> missing
) {
    public record Missing(Long productId, Long required, Long available) {}
}