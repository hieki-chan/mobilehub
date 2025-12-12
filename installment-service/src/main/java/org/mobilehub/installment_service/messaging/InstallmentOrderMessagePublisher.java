package org.mobilehub.installment_service.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstallmentOrderMessagePublisher {

    private static final String TOPIC = "installment-order-create";

    private final KafkaTemplate<String, InstallmentOrderCreateMessage> kafkaTemplate;

    public void publishInstallmentOrderCreate(InstallmentOrderCreateMessage msg) {
        String key = String.valueOf(msg.getApplicationId()); // partition theo applicationId
        kafkaTemplate.send(TOPIC, key, msg);
    }
}
