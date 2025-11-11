package org.mobilehub.inventory_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.service.InventoryService;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.mobilehub.shared.common.topics.Topics;
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

    @KafkaListener(topics = OrderTopics.ORDER_CREATED, groupId = "inventory-service")
    public void onOrderCreated(@Payload OrderCreatedEvent evt) {
        // Map các line item trong event -> entity item dùng cho reserve()
        List<InventoryReservationItem> items = evt.items().stream()
                .map(i -> {
                    InventoryReservationItem it = new InventoryReservationItem();
                    it.setProductId(i.productId());
                    it.setQuantity(i.quantity());
                    return it;
                })
                .toList();

        log.info("[inventory] Receive order.created orderId={}, items={}", evt.orderId(), items.size());

        // idempotencyKey truyền thẳng từ event để chống xử lý trùng
        inventoryService.reserve(evt.orderId(), items, evt.idempotencyKey());
    }
}