package org.mobilehub.installment_service.service.impl;

import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.dto.contract.ContractFilter;
import org.mobilehub.installment_service.dto.contract.ContractResponse;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.service.InstallmentContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentContractServiceImpl implements InstallmentContractService {

    private final InstallmentContractRepository contractRepo;

    @Override
    public List<ContractResponse> searchContracts(ContractFilter filter) {
        return contractRepo.findAll().stream()
                .filter(ct -> filter.getStatus() == null || ct.getStatus() == filter.getStatus())
                .filter(ct -> {
                    if (!StringUtils.hasText(filter.getQ())) return true;
                    String q = filter.getQ().toLowerCase();
                    return ct.getCode().toLowerCase().contains(q)
                            || ct.getApplication().getCode().toLowerCase().contains(q)
                            || ct.getApplication().getCustomerName().toLowerCase().contains(q);
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ContractResponse toResponse(InstallmentContract ct) {
        return ContractResponse.builder()
                .id(ct.getId())
                .code(ct.getCode())
                .applicationCode(ct.getApplication().getCode())
                .customerName(ct.getApplication().getCustomerName())
                .productName(ct.getApplication().getProductName())
                .planName(ct.getPlan().getName())
                .totalLoan(ct.getTotalLoan())
                .remainingAmount(ct.getRemainingAmount())
                .status(ct.getStatus())
                .build();
    }
}
