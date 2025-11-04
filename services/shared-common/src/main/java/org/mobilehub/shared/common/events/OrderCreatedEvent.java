package org.mobilehub.shared.common.events;

import java.util.List;

public record OrderCreatedEvent(
        String eventId,
        Long orderId,
        Long userId,
        String idempotencyKey,
        List<Item> items
) {
    public record Item(Long productId, Long quantity) {}
}