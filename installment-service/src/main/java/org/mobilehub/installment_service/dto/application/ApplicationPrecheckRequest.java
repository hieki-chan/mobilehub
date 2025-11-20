package org.mobilehub.installment_service.dto.application;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ApplicationPrecheckRequest {

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

    private Integer tenorMonths;

    @NotNull
    private Long partnerId;

    @NotNull
    private Long planId;

    // Thông tin để check đủ điều kiện (tùy bạn có dùng hay không)
    @NotNull @Min(18)
    private Integer customerAge;

    @NotNull @Positive
    private Long monthlyIncome;
}
