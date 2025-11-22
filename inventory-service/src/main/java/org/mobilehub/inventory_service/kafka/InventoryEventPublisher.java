package org.mobilehub.inventory_service.kafka;

import org.mobilehub.shared.common.events.InventoryCommittedEvent;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.common.events.InventoryReleasedEvent;
import org.mobilehub.shared.common.events.InventoryReservedEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public interface InventoryEventPublisher {

    void publishReserved(Long orderId, String reservationId, Instant expiresAt,
                         List<InventoryReservedEvent.Line> lines);

    void publishCommitted(Long orderId, String reservationId,
                          List<InventoryCommittedEvent.Line> lines);

    void publishReleased(Long orderId, String reservationId,
                         List<InventoryReleasedEvent.Line> lines);

    void publishRejected(Long orderId, String reason,
                         List<InventoryRejectedEvent.Missing> missing);

    // ====== overload tiện dụng (caller cũ vẫn chạy) ======
    default void publishReserved(Long orderId, String reservationId, Instant expiresAt) {
        publishReserved(orderId, reservationId, expiresAt, Collections.emptyList());
    }

    default void publishCommitted(Long orderId, String reservationId) {
        publishCommitted(orderId, reservationId, Collections.emptyList());
    }

    default void publishReleased(Long orderId, String reservationId) {
        publishReleased(orderId, reservationId, Collections.emptyList());
    }

    default void publishRejected(Long orderId, String reason) {
        publishRejected(orderId, reason, Collections.emptyList());
    }
}
