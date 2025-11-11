package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.entity.Payment;
import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.entity.WebhookEvent;
import org.mobilehub.payment_service.exception.NotFoundException;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repo.PaymentRepository;
import org.mobilehub.payment_service.repo.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentProviderGateway gateway;
    private final WebhookEventRepository eventRepo;
    private final PaymentRepository paymentRepo;

    @Transactional
    public void handle(String rawBody, Map<String, String> headers) {
        var evt = gateway.parseAndVerifyWebhook(rawBody, headers);

        // idempotency: skip if processed
        if (eventRepo.existsByEventId(evt.eventId())) return;

        WebhookEvent entity = WebhookEvent.builder()
                .eventId(evt.eventId())
                .provider("PAYOS")
                .orderCode(evt.orderCode())
                .eventType(evt.eventType())
                .occurredAt(evt.occurredAt())
                .payloadHash(HashingUtils.sha256(rawBody))
                .build();
        eventRepo.save(entity);

        // Update payment state
        Payment p = paymentRepo.findByOrderCode(evt.orderCode())
                .orElseThrow(() -> new NotFoundException("Payment not found for order " + evt.orderCode()));
        if (evt.status() == PaymentStatus.AUTHORIZED) {
            p.markAuthorized();
        } else if (evt.status() == PaymentStatus.CAPTURED) {
            // Amount handled in capture flow; here we mark final if necessary
            p.setStatus(PaymentStatus.CAPTURED);
        } else if (evt.status() == PaymentStatus.FAILED) {
            p.fail("webhook_failed", "Provider reported failure");
        }
        paymentRepo.save(p);

        entity.setPayment(p);
        eventRepo.save(entity);
    }
}
