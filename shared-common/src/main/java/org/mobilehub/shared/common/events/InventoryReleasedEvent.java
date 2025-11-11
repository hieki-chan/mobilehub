package org.mobilehub.shared.common.events;

import java.util.List;

public record InventoryReleasedEvent(
        String eventId, Long orderId, String reservationId, List<Line> lines
) {
    public record Line(Long productId, Long quantity) {}
}