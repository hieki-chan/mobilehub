package org.mobilehub.installment_service.dto.application;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ApplicationCreateRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    @NotBlank
    private String productName;

    @NotNull @Positive
    private Long productPrice;

    @NotNull @Positive
    private Long loanAmount;

    @NotNull
    private Long partnerId;

    @NotNull
    private Long planId;
}
