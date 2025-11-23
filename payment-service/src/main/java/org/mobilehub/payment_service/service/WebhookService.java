package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.client.InventoryClient;
import org.mobilehub.payment_service.client.OrderClient;
import org.mobilehub.payment_service.entity.Payment;
import org.mobilehub.payment_service.entity.PaymentStatus;
import org.mobilehub.payment_service.entity.WebhookEvent;
import org.mobilehub.payment_service.exception.NotFoundException;
import org.mobilehub.payment_service.provider.PaymentProviderGateway;
import org.mobilehub.payment_service.repository.PaymentRepository;
import org.mobilehub.payment_service.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentProviderGateway gateway;
    private final WebhookEventRepository eventRepo;
    private final PaymentRepository paymentRepo;

    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;

    @Transactional
    public void handle(String rawBody, Map<String, String> headers) {

        var evt = gateway.parseAndVerifyWebhook(rawBody, headers);

        // 0) Idempotency: nếu event đã xử lý thì bỏ qua
        if (eventRepo.existsByEventId(evt.eventId())) {
            log.info("[payment.webhook] duplicate eventId={}, skip", evt.eventId());
            return;
        }

        Payment p = paymentRepo.findByOrderCode(evt.orderCode())
                .orElseThrow(() -> new NotFoundException("Payment not found for order " + evt.orderCode()));

        // Optional: cảnh báo mismatch providerPaymentId
        if (evt.providerPaymentId() != null
                && p.getProviderPaymentId() != null
                && !evt.providerPaymentId().equals(p.getProviderPaymentId())) {

            log.warn("[payment.webhook] providerPaymentId mismatch orderCode={}, db={}, evt={}",
                    p.getOrderCode(), p.getProviderPaymentId(), evt.providerPaymentId());
        }

        boolean needCommit = false;
        boolean needRelease = false;

        PaymentStatus prevStatus = p.getStatus();

        // 1) Update trạng thái payment theo webhook
        if (evt.status() == PaymentStatus.AUTHORIZED) {
            if (prevStatus != PaymentStatus.AUTHORIZED) {
                p.markAuthorized();
            }

        } else if (evt.status() == PaymentStatus.CAPTURED) {
            // đồng bộ capturedAmount cho AUTOMATIC capture
            if (evt.amount() != null) {
                p.setCapturedAmount(evt.amount()); // set thẳng để idempotent
            }
            if (prevStatus != PaymentStatus.CAPTURED) {
                p.setStatus(PaymentStatus.CAPTURED);
                needCommit = true;
            }

        } else if (evt.status() == PaymentStatus.FAILED) {
            if (prevStatus != PaymentStatus.FAILED) {
                String code = evt.errorCode() != null ? evt.errorCode() : "webhook_failed";
                String msg  = evt.errorMessage() != null ? evt.errorMessage() : "Provider reported failure";
                p.fail(code, msg);
                needRelease = true;
            }

        } else if (evt.status() == PaymentStatus.CANCELED) {
            if (prevStatus != PaymentStatus.CANCELED) {
                // ✅ không gọi fail() vì fail() ép status=FAILED
                p.setStatus(PaymentStatus.CANCELED);
                p.setFailureCode(evt.errorCode() != null ? evt.errorCode() : "webhook_canceled");
                p.setFailureMessage(evt.errorMessage() != null ? evt.errorMessage() : "Provider reported canceled");
                needRelease = true;
            }

        } else {
            log.info("[payment.webhook] ignore status={} orderCode={}", evt.status(), evt.orderCode());
        }

        paymentRepo.save(p);

        // 2) Lấy reservationId từ order-service
        String reservationId = null;
        try {
            var resDto = orderClient.getReservation(p.getOrderCode()); // orderCode = orderId
            reservationId = resDto.reservationId();
        } catch (Exception ex) {
            log.warn("[payment.webhook] cannot fetch reservationId for orderCode={}, reason={}",
                    p.getOrderCode(), ex.getMessage());
        }

        // 3) success -> commit, fail/cancel -> release
        if (reservationId != null && !reservationId.isBlank()) {
            if (needCommit) {
                log.info("[payment.webhook] CAPTURED -> commit inventory reservationId={}", reservationId);
                inventoryClient.commit(reservationId);
            } else if (needRelease) {
                log.info("[payment.webhook] FAILED/CANCELED -> release inventory reservationId={}", reservationId);
                inventoryClient.release(reservationId);
            }
        } else if (needCommit || needRelease) {
            log.warn("[payment.webhook] reservationId missing for orderCode={}, skip inventory action",
                    p.getOrderCode());
        }

        // 4) Lưu webhook event SAU khi side-effect OK
        WebhookEvent entity = WebhookEvent.builder()
                .eventId(evt.eventId())
                .provider(evt.provider() != null ? evt.provider() : "PAYOS")
                .orderCode(evt.orderCode())
                .eventType(evt.eventType())
                .occurredAt(evt.occurredAt())
                .payloadHash(HashingUtils.sha256(rawBody))
                .payment(p)
                .build();

        eventRepo.save(entity);

        log.info("[payment.webhook] processed eventId={}, orderCode={}, status={} (prevStatus={})",
                evt.eventId(), evt.orderCode(), evt.status(), prevStatus);
    }
}
