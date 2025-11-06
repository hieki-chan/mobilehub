package org.mobilehub.order_service.kafka;

import lombok.RequiredArgsConstructor;
import org.mobilehub.shared.common.events.OrderCreatedEvent;
import org.mobilehub.shared.common.topics.Topics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafka;

    public void publish(OrderCreatedEvent evt) {
        // key = orderId để giữ thứ tự theo từng đơn
        kafka.send(Topics.ORDER_CREATED, evt.orderId().toString(), evt);
    }
}