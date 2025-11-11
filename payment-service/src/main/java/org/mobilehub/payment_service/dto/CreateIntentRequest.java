package org.mobilehub.payment_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.mobilehub.payment_service.entity.CaptureMethod;

public record CreateIntentRequest(
    @NotNull Long orderCode,
    @NotNull @Positive BigDecimal amount,
    @NotBlank String currency,
    @NotNull CaptureMethod captureMethod,
    @NotBlank String channel,
    @NotBlank String returnUrl,
    String provider
) { }
