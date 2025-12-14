package org.mobilehub.installment_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mobilehub.installment_service.messaging.InstallmentOrderCreateMessage;
import org.mobilehub.shared.contracts.notification.InstallmentApprovedEvent;
import org.mobilehub.shared.contracts.notification.InstallmentPaymentDueEvent;
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

    @Bean
    public ProducerFactory<String, InstallmentOrderCreateMessage> installmentProducerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // tối ưu cho cross-service: không nhét type header (đỡ lỗi mismatch class/package)
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
    // NOTIFICATION EVENTS PRODUCER
    // ============================================================
    @Bean("notificationProducerFactory")
    public ProducerFactory<String, Object> notificationProducerFactory(Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        
        // Debug log
        System.out.println("[KAFKA-CONFIG] Creating notificationProducerFactory");
        
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("notificationKafkaTemplate")
    @org.springframework.context.annotation.Primary  // Đặt làm primary để tránh conflict
    public KafkaTemplate<String, Object> notificationKafkaTemplate(
            @org.springframework.beans.factory.annotation.Qualifier("notificationProducerFactory") 
            ProducerFactory<String, Object> notificationProducerFactory
    ) {
        System.out.println("[KAFKA-CONFIG] Creating notificationKafkaTemplate with factory: " + notificationProducerFactory);
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(notificationProducerFactory);
        System.out.println("[KAFKA-CONFIG] Created template: " + template);
        return template;
    }
}
