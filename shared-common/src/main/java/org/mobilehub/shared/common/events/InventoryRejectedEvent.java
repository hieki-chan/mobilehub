package org.mobilehub.shared.common.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Event bắn ra khi Inventory không reserve được hàng cho Order.
 * Order-service sẽ nhận event này để chuyển trạng thái đơn sang REJECTED/OUT_OF_STOCK.
 */
public record InventoryRejectedEvent(
        String eventId,
        Long orderId,
        String reason,
        List<Missing> missing,
        Instant occurredAt
) {
    /**
     * Constructor tiện: tự sinh eventId + occurredAt.
     */
    public InventoryRejectedEvent(Long orderId, String reason, List<Missing> missing) {
        this(UUID.randomUUID().toString(), orderId, reason, missing, Instant.now());
    }

    /**
     * Mô tả một line bị thiếu hàng.
     * - productId: sản phẩm bị thiếu
     * - requestedQty: số lượng order yêu cầu
     * - availableQty: số lượng thực tế còn available tại thời điểm reserve
     */
    public record Missing(
            Long productId,
            Long requestedQty,
            Long availableQty
    ) {}
}
