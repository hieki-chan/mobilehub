package org.mobilehub.inventory_service.kafka;

import org.mobilehub.shared.common.events.InventoryCommittedEvent;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.common.events.InventoryReleasedEvent;
import org.mobilehub.shared.common.events.InventoryReservedEvent;

import java.time.Instant;
import java.util.List;

public interface InventoryEventPublisher {

    void publishReserved(Long orderId,
                         String reservationId,
                         Instant expiresAt,
                         List<InventoryReservedEvent.Line> lines);

    void publishCommitted(Long orderId,
                          String reservationId,
                          List<InventoryCommittedEvent.Line> lines);

    void publishReleased(Long orderId,
                         String reservationId,
                         List<InventoryReleasedEvent.Line> lines);

    void publishRejected(Long orderId,
                         String reason,
                         List<InventoryRejectedEvent.Missing> missing);
}
