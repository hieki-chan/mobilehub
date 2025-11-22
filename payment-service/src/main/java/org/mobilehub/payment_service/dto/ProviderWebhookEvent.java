package org.mobilehub.payment_service.dto;

import org.mobilehub.payment_service.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ProviderWebhookEvent(
        String eventId,
        String provider,        // "PAYOS"
        Long orderCode,         // = orderId bên order-service
        String eventType,       // raw type để log
        Instant occurredAt,
        PaymentStatus status,   // AUTHORIZED / CAPTURED / FAILED / CANCELED(CANCELLED)
        BigDecimal amount,      // số tiền capture (có thể null)
        String errorCode,       // có thể null
        String errorMessage     // có thể null
) {}
