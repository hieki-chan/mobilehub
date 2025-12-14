package org.mobilehub.installment_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mobilehub.installment_service.messaging.InstallmentOrderCreateMessage;
import org.mobilehub.shared.contracts.notification.InstallmentApprovedEvent;
import org.mobilehub.shared.contracts.notification.InstallmentPaymentDueEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // ============================================================
    // INSTALLMENT ORDER PRODUCER (giữ nguyên)
    // ============================================================
    @Bean
    public ProducerFactory<String, InstallmentOrderCreateMessage> installmentProducerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, InstallmentOrderCreateMessage> installmentKafkaTemplate(
            ProducerFactory<String, InstallmentOrderCreateMessage> pf
    ) {
        return new KafkaTemplate<>(pf);
    }

    // ============================================================
    // ✅ NOTIFICATION EVENTS PRODUCER - InstallmentApprovedEvent
    // ============================================================
    @Bean("installmentApprovedProducerFactory")
    public ProducerFactory<String, InstallmentApprovedEvent> installmentApprovedProducerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("installmentApprovedKafkaTemplate")
    public KafkaTemplate<String, InstallmentApprovedEvent> installmentApprovedKafkaTemplate(
            @Qualifier("installmentApprovedProducerFactory")
            ProducerFactory<String, InstallmentApprovedEvent> pf
    ) {
        return new KafkaTemplate<>(pf);
    }

    // ============================================================
    // ✅ NOTIFICATION EVENTS PRODUCER - InstallmentPaymentDueEvent
    // ============================================================
    @Bean("installmentPaymentDueProducerFactory")
    public ProducerFactory<String, InstallmentPaymentDueEvent> installmentPaymentDueProducerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("installmentPaymentDueKafkaTemplate")
    public KafkaTemplate<String, InstallmentPaymentDueEvent> installmentPaymentDueKafkaTemplate(
            @Qualifier("installmentPaymentDueProducerFactory")
            ProducerFactory<String, InstallmentPaymentDueEvent> pf
    ) {
        return new KafkaTemplate<>(pf);
    }
}