package org.mobilehub.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.order_service.service.OrderStatusUpdater;
import org.mobilehub.shared.common.events.InventoryCommittedEvent;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.common.events.InventoryReleasedEvent;
import org.mobilehub.shared.common.events.InventoryReservedEvent;
import org.mobilehub.shared.common.topics.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryListeners {

    private final OrderStatusUpdater updater;

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "order-service")
    public void onReserved(@Payload InventoryReservedEvent evt) {
        log.info("[order] Receive INVENTORY_RESERVED orderId={}, reservationId={}, expiresAt={}, lines={}",
                evt.orderId(),
                evt.reservationId(),
                evt.expiresAt(),
                evt.lines() != null ? evt.lines().size() : 0
        );

        updater.markAwaitingPayment(
                evt.orderId(),
                evt.reservationId(),
                evt.expiresAt()
        );
    }

    @KafkaListener(topics = Topics.INVENTORY_COMMITTED, groupId = "order-service")
    public void onCommitted(@Payload InventoryCommittedEvent evt) {
        log.info("[order] Receive INVENTORY_COMMITTED orderId={}, reservationId={}, lines={}",
                evt.orderId(),
                evt.reservationId(),
                evt.lines() != null ? evt.lines().size() : 0
        );

        updater.markPaid(evt.orderId());
    }

    @KafkaListener(topics = Topics.INVENTORY_RELEASED, groupId = "order-service")
    public void onReleased(@Payload InventoryReleasedEvent evt) {
        log.info("[order] Receive INVENTORY_RELEASED orderId={}, reservationId={}, lines={}",
                evt.orderId(),
                evt.reservationId(),
                evt.lines() != null ? evt.lines().size() : 0
        );

        // Event released không có reason, nên dùng default
        updater.markReleased(evt.orderId(), "RELEASED");
    }

    @KafkaListener(topics = Topics.INVENTORY_REJECTED, groupId = "order-service")
    public void onRejected(@Payload InventoryRejectedEvent evt) {
        log.info("[order] Receive INVENTORY_REJECTED orderId={}, reason={}, missing={}",
                evt.orderId(),
                evt.reason(),
                evt.missing() != null ? evt.missing().size() : 0
        );

        updater.markRejected(evt.orderId(), evt.reason());
    }
}
