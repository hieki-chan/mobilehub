package org.mobilehub.notification_service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.mobilehub.shared.contracts.notification.InstallmentApprovedEvent;
import org.mobilehub.shared.contracts.notification.InstallmentPaymentDueEvent;
import org.mobilehub.shared.contracts.notification.PaymentCapturedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ============================================================
    // BASE CONFIG
    // ============================================================
    private Map<String, Object> baseConsumerProps(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }

    // ============================================================
    // PAYMENT CAPTURED - PaymentCapturedEvent
    // ============================================================
    @Bean
    public ConsumerFactory<String, PaymentCapturedEvent> paymentCapturedConsumerFactory() {
        Map<String, Object> props = baseConsumerProps("notification-service");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PaymentCapturedEvent.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCapturedEvent> paymentCapturedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentCapturedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentCapturedConsumerFactory());
        return factory;
    }

    // ============================================================
    // INSTALLMENT APPROVED - InstallmentApprovedEvent
    // ============================================================
    @Bean
    public ConsumerFactory<String, InstallmentApprovedEvent> installmentApprovedConsumerFactory() {
        Map<String, Object> props = baseConsumerProps("notification-service-installment");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InstallmentApprovedEvent.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InstallmentApprovedEvent> installmentApprovedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InstallmentApprovedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(installmentApprovedConsumerFactory());
        return factory;
    }

    // ============================================================
    // INSTALLMENT PAYMENT DUE - InstallmentPaymentDueEvent
    // ============================================================
    @Bean
    public ConsumerFactory<String, InstallmentPaymentDueEvent> installmentPaymentDueConsumerFactory() {
        Map<String, Object> props = baseConsumerProps("notification-service-payment-due");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InstallmentPaymentDueEvent.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InstallmentPaymentDueEvent> installmentPaymentDueListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InstallmentPaymentDueEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(installmentPaymentDueConsumerFactory());
        return factory;
    }
}