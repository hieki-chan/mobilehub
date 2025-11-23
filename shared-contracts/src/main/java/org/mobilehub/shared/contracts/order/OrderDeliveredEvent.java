package org.mobilehub.shared.contracts.order;

import java.util.List;

public record OrderDeliveredEvent(
        Long orderId,
        Long userId,
        String idempotencyKey,
        List<Item> items
) {
    public record Item(Long productId, Long variantId, Integer quantity, Integer sold) {}
}