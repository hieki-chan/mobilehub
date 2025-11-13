package org.mobilehub.order_service.kafka;

import lombok.RequiredArgsConstructor;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.mobilehub.shared.contracts.order.OrderTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafka;

    public void publish(OrderCreatedEvent evt) {
        kafka.send(OrderTopics.ORDER_CREATED, evt);
    }
}