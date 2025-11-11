package org.mobilehub.payment_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RefundRequest(
    @NotNull Long paymentId,
    @NotNull @Positive BigDecimal amount
) { }
