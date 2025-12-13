package org.mobilehub.payment_service.kafka;

import lombok.RequiredArgsConstructor;
import org.mobilehub.shared.contracts.notification.PaymentCapturedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPaymentEventPublisher implements PaymentEventPublisher {

    public static final String TOPIC_PAYMENT_CAPTURED = "payment.captured";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishPaymentCaptured(PaymentCapturedEvent event) {
        String key = (event.orderId() == null) ? null : String.valueOf(event.orderId());
        kafkaTemplate.send(TOPIC_PAYMENT_CAPTURED, key, event);
    }
}
