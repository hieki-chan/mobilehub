package org.mobilehub.payment_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private static final String TOPIC = "payment.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void send(String key, PaymentEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, key, json);
            log.info("[payment.events] sent: key={}, type={}, orderCode={}", key, event.getType(), event.getOrderCode());
        } catch (Exception e) {
            log.error("[payment.events] send error: {}", e.getMessage(), e);
        }
    }

    public void publishSucceeded(Long orderCode, Long amount, String providerCode, String reference) {
        PaymentEvent evt = PaymentEvent.builder()
                .type("PAYMENT_SUCCEEDED")
                .orderCode(orderCode)
                .amount(amount)
                .currency("VND")
                .provider("payos")
                .providerCode(providerCode)
                .reference(reference)
                .at(java.time.OffsetDateTime.now())
                .build();
        send(String.valueOf(orderCode), evt);
    }

    public void publishFailed(Long orderCode, Long amount, String providerCode, String reference) {
        PaymentEvent evt = PaymentEvent.builder()
                .type("PAYMENT_FAILED")
                .orderCode(orderCode)
                .amount(amount)
                .currency("VND")
                .provider("payos")
                .providerCode(providerCode)
                .reference(reference)
                .at(java.time.OffsetDateTime.now())
                .build();
        send(String.valueOf(orderCode), evt);
    }

    public void publishCanceled(Long orderCode, Long amount, String providerCode, String reference) {
        PaymentEvent evt = PaymentEvent.builder()
                .type("PAYMENT_CANCELED")
                .orderCode(orderCode)
                .amount(amount)
                .currency("VND")
                .provider("payos")
                .providerCode(providerCode)
                .reference(reference)
                .at(java.time.OffsetDateTime.now())
                .build();
        send(String.valueOf(orderCode), evt);
    }
}
