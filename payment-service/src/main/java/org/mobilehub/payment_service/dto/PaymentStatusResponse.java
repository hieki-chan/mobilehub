package org.mobilehub.payment_service.dto;

import java.math.BigDecimal;
import org.mobilehub.payment_service.entity.PaymentStatus;

public record PaymentStatusResponse(
    Long paymentId,
    Long orderCode,
    PaymentStatus status,
    BigDecimal amount,
    BigDecimal capturedAmount,
    String providerPaymentId
) { }
