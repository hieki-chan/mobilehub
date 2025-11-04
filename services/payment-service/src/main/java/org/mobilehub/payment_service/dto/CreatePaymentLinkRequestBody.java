package org.mobilehub.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CreatePaymentLinkRequestBody {
    @NotNull
    @Positive
    private Long orderCode;
    @NotBlank
    private String productName;
    private String description;
    @NotNull
    private Long amount;
    @NotBlank
    @URL
    private String returnUrl;
    @NotBlank
    @URL
    private String cancelUrl;
    @NotNull
    @Positive
    private Integer quantity = 1;
}
