package org.mobilehub.payment_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateIntentRequest(
        Long orderId,
        Long orderCode,
        BigDecimal amount,
        String returnUrl
) {}

