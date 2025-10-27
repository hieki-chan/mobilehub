package org.mobilehub.order_service.kafka;

import lombok.RequiredArgsConstructor;

import org.mobilehub.order_service.service.OrderStatusUpdater;
import org.mobilehub.shared.common.events.InventoryCommittedEvent;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.common.events.InventoryReleasedEvent;
import org.mobilehub.shared.common.events.InventoryReservedEvent;
import org.mobilehub.shared.common.topics.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryListeners {
    private final OrderStatusUpdater updater;

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "order-service")
    public void onReserved(@Payload InventoryReservedEvent evt) {
        updater.markAwaitingPayment(evt.orderId(), evt.reservationId(), evt.expiresAt());
    }

    @KafkaListener(topics = Topics.INVENTORY_COMMITTED, groupId = "order-service")
    public void onCommitted(@Payload InventoryCommittedEvent evt) {
        updater.markPaid(evt.orderId());
    }

    @KafkaListener(topics = Topics.INVENTORY_RELEASED, groupId = "order-service")
    public void onReleased(@Payload InventoryReleasedEvent evt) {
        updater.markReleased(evt.orderId(), "RELEASED");
    }

    @KafkaListener(topics = Topics.INVENTORY_REJECTED, groupId = "order-service")
    public void onRejected(@Payload InventoryRejectedEvent evt) {
        updater.markRejected(evt.orderId(), evt.reason());
    }
}
