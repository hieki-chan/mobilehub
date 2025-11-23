package org.mobilehub.inventory_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    // ❌ BỎ RecordMessageConverter để tránh double-convert
    // JsonDeserializer ở consumer sẽ tự map JSON -> OrderCreatedEvent

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryReservedTopic() {
        return TopicBuilder.name("inventory.reserved")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryCommittedTopic() {
        return TopicBuilder.name("inventory.committed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryReleasedTopic() {
        return TopicBuilder.name("inventory.released")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryRejectedTopic() {
        return TopicBuilder.name("inventory.rejected")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Cấu hình Kafka producer cho việc gửi các message
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        // Cấu hình serializer cho key (String) và value (JsonSerializer)
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", "localhost:9092");

        // Cấu hình key là String, value là Object (sẽ được tự động serialize thành JSON)
        configs.put("key.serializer", StringSerializer.class);

        // Dùng JsonSerializer cho giá trị (value)
        configs.put("value.serializer", JsonSerializer.class);

        return configs;
    }


}
