package org.mobilehub.inventory_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.mobilehub.shared.common.topics.Topics;
import org.mobilehub.shared.contracts.order.OrderTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
@EnableKafka
public class KafkaConfig {

    // Dùng Jackson để convert JSON <-> Java record/POJO
    @Bean
    public RecordMessageConverter recordMessageConverter() {
        return new StringJsonMessageConverter();
    }

    // Dev: tự tạo topic nếu broker cho phép (prod nên tạo bằng ops/terraform)
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(OrderTopics.ORDER_CREATED).partitions(3).replicas(1).build();
    }
}
