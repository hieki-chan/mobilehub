package org.mobilehub.shared.contracts.notification;

import java.math.BigDecimal;

public record PaymentCapturedEvent(
        Long orderId,
        String userId,
        BigDecimal amount,
        String currency,
        String userEmail
) {}
