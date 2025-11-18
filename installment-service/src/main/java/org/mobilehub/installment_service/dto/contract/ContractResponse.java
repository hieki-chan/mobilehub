package org.mobilehub.installment_service.dto.contract;

import org.mobilehub.installment_service.domain.enums.ContractStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractResponse {
    private Long id;
    private String code;
    private String applicationCode;
    private String customerName;
    private String productName;
    private String planName;
    private Long totalLoan;
    private Long remainingAmount;
    private ContractStatus status;
}
