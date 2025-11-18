package org.mobilehub.installment_service.controller;

import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.dto.contract.ContractFilter;
import org.mobilehub.installment_service.dto.contract.ContractResponse;
import org.mobilehub.installment_service.service.InstallmentContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class InstallmentContractController {

    private final InstallmentContractService contractService;

    @GetMapping
    public List<ContractResponse> search(
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) String q
    ) {
        ContractFilter filter = new ContractFilter();
        filter.setStatus(status);
        filter.setQ(q);
        return contractService.searchContracts(filter);
    }
}
