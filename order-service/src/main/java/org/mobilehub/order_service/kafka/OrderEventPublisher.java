package org.mobilehub.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.mobilehub.shared.contracts.order.OrderDeliveredEvent;
import org.mobilehub.shared.contracts.order.OrderGroup;
import org.mobilehub.shared.contracts.order.OrderTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public void publish(OrderCreatedEvent evt) {
        String key = String.valueOf(evt.orderId());
        kafka.send(OrderTopics.ORDER_CREATED, key, evt);
        log.info("[order] Published ORDER_CREATED orderId={}, items={}",
                evt.orderId(),
                evt.items() != null ? evt.items().size() : 0
        );
    }

    public void publishOrderDeliveredEvent(OrderDeliveredEvent evt){
        kafka.send(OrderTopics.ORDER_DELIVERED, String.valueOf(evt.orderId()), evt);
    }
}
