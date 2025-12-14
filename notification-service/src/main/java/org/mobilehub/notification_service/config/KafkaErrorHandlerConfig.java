package org.mobilehub.notification_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    /**
     * Error handler để skip bad messages thay vì retry vô hạn
     * Useful khi có message lỗi trong Kafka topic
     */
    @Bean
    public DefaultErrorHandler errorHandler() {
        // FixedBackOff(interval=0, maxAttempts=0) = không retry, skip ngay
        DefaultErrorHandler handler = new DefaultErrorHandler((record, exception) -> {
            log.error("[KAFKA-ERROR] Skipping bad message: topic={}, partition={}, offset={}, key={}, error={}", 
                    record.topic(), 
                    record.partition(), 
                    record.offset(),
                    record.key(),
                    exception.getMessage());
            log.error("[KAFKA-ERROR] Message value: {}", record.value());
            log.error("[KAFKA-ERROR] Full exception:", exception);
        }, new FixedBackOff(0L, 0L));

        return handler;
    }
}
