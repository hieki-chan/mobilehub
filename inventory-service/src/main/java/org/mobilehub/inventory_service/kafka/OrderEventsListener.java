package org.mobilehub.inventory_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.service.InventoryService;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.mobilehub.shared.contracts.order.OrderTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventsListener {

    private final InventoryService inventoryService;
    private final InventoryEventPublisher publisher;

    @KafkaListener(topics = OrderTopics.ORDER_CREATED, groupId = "inventory-service")
    public void onOrderCreated(@Payload OrderCreatedEvent evt) {

        List<InventoryReservationItem> items = evt.items().stream()
                .map(i -> {
                    InventoryReservationItem it = new InventoryReservationItem();
                    it.setProductId(i.productId());
                    it.setQuantity(i.quantity().longValue());
                    return it;
                })
                .toList();

        log.info("[inventory] Receive order.created orderId={}, items={}", evt.orderId(), items.size());

        try {
            inventoryService.reserve(evt.orderId(), items, evt.idempotencyKey());
        } catch (Exception ex) {
            log.warn("[inventory] Reserve failed for orderId={}, reason={}", evt.orderId(), ex.getMessage());

            // Shared event requires missing list. If you don’t compute detail yet, send empty list safely.
            List<InventoryRejectedEvent.Missing> missing = List.of();

            publisher.publishRejected(
                    evt.orderId(),
                    ex.getMessage(),
                    missing
            );

            // IMPORTANT: don’t throw -> avoid Kafka retry loop blocking partition
        }
    }
}
