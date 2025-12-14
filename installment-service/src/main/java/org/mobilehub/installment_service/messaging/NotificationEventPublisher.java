package org.mobilehub.installment_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.shared.contracts.notification.InstallmentApprovedEvent;
import org.mobilehub.shared.contracts.notification.InstallmentPaymentDueEvent;
import org.mobilehub.shared.contracts.notification.NotificationTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventPublisher {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("notificationKafkaTemplate")
    private KafkaTemplate<String, Object> notificationKafkaTemplate;

    /**
     * Gửi event khi hồ sơ trả góp được duyệt
     */
    public void publishInstallmentApproved(InstallmentApprovedEvent event) {
        String topic = NotificationTopics.INSTALLMENT_APPROVED;
        log.info("[KAFKA] BEFORE SEND - Topic: {}, Event type: {}, Event: {}",
                topic, event.getClass().getName(), event);

        try {
            notificationKafkaTemplate.send(topic, event);
            log.info("[KAFKA] Published InstallmentApprovedEvent to topic={}: applicationId={}, userId={}",
                    topic, event.applicationId(), event.userId());
        } catch (Exception e) {
            log.error("[KAFKA] Failed to publish InstallmentApprovedEvent to topic={}: applicationId={}",
                    topic, event.applicationId(), e);
        }
    }

    /**
     * Gửi event khi đến hạn thanh toán trả góp
     */
    public void publishInstallmentPaymentDue(InstallmentPaymentDueEvent event) {
        try {
            notificationKafkaTemplate.send(NotificationTopics.INSTALLMENT_PAYMENT_DUE, event);
            log.info("[KAFKA] Published InstallmentPaymentDueEvent: contractId={}, paymentId={}, userId={}", 
                    event.contractId(), event.paymentId(), event.userId());
        } catch (Exception e) {
            log.error("[KAFKA] Failed to publish InstallmentPaymentDueEvent: paymentId={}", 
                    event.paymentId(), e);
        }
    }
}
