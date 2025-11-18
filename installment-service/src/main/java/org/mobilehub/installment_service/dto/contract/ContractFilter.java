package org.mobilehub.installment_service.dto.contract;

import org.mobilehub.installment_service.domain.enums.ContractStatus;
import lombok.Data;

@Data
public class ContractFilter {
    private ContractStatus status;
    private String q;
}
