package org.mobilehub.installment_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.mobilehub.shared.contracts.notification.InstallmentApprovedEvent;
import org.mobilehub.shared.contracts.notification.InstallmentPaymentDueEvent;
import org.mobilehub.shared.contracts.notification.NotificationTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventPublisher {

    // ✅ Inject đúng template cho từng event type
    private final KafkaTemplate<String, InstallmentApprovedEvent> installmentApprovedTemplate;
    private final KafkaTemplate<String, InstallmentPaymentDueEvent> installmentPaymentDueTemplate;

    @Autowired
    public NotificationEventPublisher(
            @Qualifier("installmentApprovedKafkaTemplate")
            KafkaTemplate<String, InstallmentApprovedEvent> installmentApprovedTemplate,

            @Qualifier("installmentPaymentDueKafkaTemplate")
            KafkaTemplate<String, InstallmentPaymentDueEvent> installmentPaymentDueTemplate
    ) {
        this.installmentApprovedTemplate = installmentApprovedTemplate;
        this.installmentPaymentDueTemplate = installmentPaymentDueTemplate;
    }

    /**
     * Gửi event khi hồ sơ trả góp được duyệt
     */
    public void publishInstallmentApproved(InstallmentApprovedEvent event) {
        String topic = NotificationTopics.INSTALLMENT_APPROVED;
        log.info("[KAFKA] Sending InstallmentApprovedEvent to topic={}: applicationId={}, userId={}, planName={}, tenorMonths={}, userEmail={}",
                topic, event.applicationId(), event.userId(), event.planName(), event.tenorMonths(), event.userEmail());

        try {
            installmentApprovedTemplate.send(topic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("[KAFKA] ✅ Published InstallmentApprovedEvent successfully: topic={}, partition={}, offset={}, applicationId={}, userId={}",
                                    topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(),
                                    event.applicationId(),
                                    event.userId());
                        } else {
                            log.error("[KAFKA] ❌ Failed to publish InstallmentApprovedEvent to topic={}: applicationId={}",
                                    topic, event.applicationId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("[KAFKA] ❌ Exception publishing InstallmentApprovedEvent to topic={}: applicationId={}",
                    topic, event.applicationId(), e);
        }
    }

    /**
     * Gửi event khi đến hạn thanh toán trả góp
     */
    public void publishInstallmentPaymentDue(InstallmentPaymentDueEvent event) {
        String topic = NotificationTopics.INSTALLMENT_PAYMENT_DUE;
        log.info("[KAFKA] Sending InstallmentPaymentDueEvent to topic={}: contractId={}, paymentId={}, userId={}, dueDate={}, amountDue={}",
                topic, event.contractId(), event.paymentId(), event.userId(), event.dueDate(), event.amountDue());

        try {
            installmentPaymentDueTemplate.send(topic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("[KAFKA] ✅ Published InstallmentPaymentDueEvent successfully: topic={}, partition={}, offset={}, contractId={}, paymentId={}",
                                    topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(),
                                    event.contractId(),
                                    event.paymentId());
                        } else {
                            log.error("[KAFKA] ❌ Failed to publish InstallmentPaymentDueEvent to topic={}: paymentId={}",
                                    topic, event.paymentId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("[KAFKA] ❌ Exception publishing InstallmentPaymentDueEvent to topic={}: paymentId={}",
                    topic, event.paymentId(), e);
        }
    }
}