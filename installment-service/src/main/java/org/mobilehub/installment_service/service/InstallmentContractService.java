package org.mobilehub.installment_service.service;

import org.mobilehub.installment_service.dto.contract.ContractFilter;
import org.mobilehub.installment_service.dto.contract.ContractResponse;

import java.util.List;

public interface InstallmentContractService {
    List<ContractResponse> searchContracts(ContractFilter filter);
}
