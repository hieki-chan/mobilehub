package org.mobilehub.notification_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.mobilehub.shared.contracts.notification.NotificationTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Nếu topic đã được tạo ở service khác thì có thể xóa file này.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentCapturedTopic() {
        return TopicBuilder.name(NotificationTopics.PAYMENT_CAPTURED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic installmentApprovedTopic() {
        return TopicBuilder.name(NotificationTopics.INSTALLMENT_APPROVED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic installmentPaymentDueTopic() {
        return TopicBuilder.name(NotificationTopics.INSTALLMENT_PAYMENT_DUE).partitions(3).replicas(1).build();
    }
}
