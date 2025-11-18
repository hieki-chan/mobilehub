package org.mobilehub.installment_service.dto.plan;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlanCreateRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private Long partnerId;

    @NotNull
    @PositiveOrZero
    private Long minPrice;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer downPaymentPercent;

    @NotNull
    @PositiveOrZero
    private Double interestRate;

    @NotBlank
    private String allowedTenors;

    private boolean active = true;
}
