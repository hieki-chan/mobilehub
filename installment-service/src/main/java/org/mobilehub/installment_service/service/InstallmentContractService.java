package org.mobilehub.installment_service.service;

import org.mobilehub.installment_service.dto.contract.ContractDetailResponse;
import org.mobilehub.installment_service.dto.contract.ContractFilter;
import org.mobilehub.installment_service.dto.contract.ContractResponse;

import java.util.List;

public interface InstallmentContractService {

    // List hợp đồng (đang có)
    List<ContractResponse> searchContracts(ContractFilter filter);

    // Chi tiết 1 hợp đồng
    ContractDetailResponse getContractDetail(Long id);
}
