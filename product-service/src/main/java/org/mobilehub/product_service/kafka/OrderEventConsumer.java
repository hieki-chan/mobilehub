package org.mobilehub.product_service.kafka;

import lombok.AllArgsConstructor;
import org.mobilehub.product_service.repository.ProductRepository;
import org.mobilehub.product_service.repository.ProductVariantRepository;
import org.mobilehub.shared.contracts.order.OrderDeliveredEvent;
import org.mobilehub.shared.contracts.order.OrderTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@SuppressWarnings("unused")
public class OrderEventConsumer {
    private final ProductVariantRepository variantRepository;

    @KafkaListener(
            topics = OrderTopics.ORDER_DELIVERED,
            groupId = "product-service-group"
    )
    public void handleOrderDelivered(@Payload OrderDeliveredEvent event) {
        event.items().forEach(this::setSoldCount);
    }

    private void setSoldCount(OrderDeliveredEvent.Item item) {
        variantRepository.findById(item.variantId()).ifPresent(variant -> {
            variant.setSold(item.sold());
        });
    }
}
