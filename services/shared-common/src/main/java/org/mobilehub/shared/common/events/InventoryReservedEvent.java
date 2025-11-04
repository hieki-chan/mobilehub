package org.mobilehub.shared.common.events;

import java.time.Instant;
import java.util.List;

public record InventoryReservedEvent(
        String eventId,
        Long orderId,
        String reservationId,
        Instant expiresAt,
        List<Line> lines
) {
    public record Line(Long productId, Long quantity) {}
}