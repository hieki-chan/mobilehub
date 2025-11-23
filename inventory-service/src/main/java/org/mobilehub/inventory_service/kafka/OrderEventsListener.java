package org.mobilehub.inventory_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.exception.InsufficientStockException;
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

        log.info("[inventory] Receive order.created orderId={}", evt.orderId());

        try {
            if (evt.items() == null || evt.items().isEmpty()) {
                throw new IllegalArgumentException("OrderCreatedEvent.items is null/empty");
            }

            List<InventoryReservationItem> items = evt.items().stream()
                    .map(i -> {
                        if (i.productId() == null) {
                            throw new IllegalArgumentException("Missing productId");
                        }
                        if (i.quantity() == null) {
                            throw new IllegalArgumentException("Missing quantity");
                        }

                        InventoryReservationItem it = new InventoryReservationItem();
                        it.setProductId(i.productId());
                        it.setQuantity(i.quantity().longValue());
                        return it;
                    })
                    .toList();

            inventoryService.reserve(evt.orderId(), items, evt.idempotencyKey());

        } catch (InsufficientStockException ex) {
            // ✅ Thiếu hàng: publishRejected kèm missing thật (KHÔNG map lại)
            log.warn("[inventory] Reserve failed (out of stock) orderId={}, reason={}",
                    evt.orderId(), ex.getMessage());

            List<InventoryRejectedEvent.Missing> missing = ex.getMissing();
            publisher.publishRejected(evt.orderId(), ex.getMessage(), missing);

        } catch (Exception ex) {
            // ✅ Lỗi khác: missing rỗng như cũ
            log.warn("[inventory] Reserve failed for orderId={}, reason={}", evt.orderId(), ex.getMessage());

            publisher.publishRejected(evt.orderId(), ex.getMessage(), List.of());
            // không throw để tránh Kafka retry loop
        }
    }
}
