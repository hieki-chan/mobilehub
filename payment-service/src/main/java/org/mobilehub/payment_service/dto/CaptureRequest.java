package org.mobilehub.payment_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CaptureRequest(
    @NotNull @Positive BigDecimal amount
) { }
