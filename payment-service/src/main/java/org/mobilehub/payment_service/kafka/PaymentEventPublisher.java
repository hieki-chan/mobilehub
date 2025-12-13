package org.mobilehub.payment_service.kafka;

import org.mobilehub.shared.contracts.notification.PaymentCapturedEvent;

public interface PaymentEventPublisher {
    void publishPaymentCaptured(PaymentCapturedEvent event);
}
