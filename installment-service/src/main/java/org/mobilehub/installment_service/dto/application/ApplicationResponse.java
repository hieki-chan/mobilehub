package org.mobilehub.installment_service.dto.application;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private String code;
    private String customerName;
    private String customerPhone;
    private String productName;
    private Long productPrice;
    private Long loanAmount;
    private String partnerName;
    private String planName;
    private ApplicationStatus status;
}
