package org.mobilehub.payment_service.dto;

import org.mobilehub.payment_service.entity.PaymentStatus;

public record CreateIntentResponse(
    Long paymentId,
    Long orderCode,
    PaymentStatus status,
    String paymentUrl,
    String clientSecret,
    String providerPaymentId
) { }
