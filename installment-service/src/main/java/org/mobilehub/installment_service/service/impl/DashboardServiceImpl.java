package org.mobilehub.installment_service.service.impl;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.dto.dashboard.DashboardOverviewDto;
import org.mobilehub.installment_service.repository.InstallmentApplicationRepository;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final InstallmentApplicationRepository applicationRepo;
    private final InstallmentContractRepository contractRepo;

    @Override
    public DashboardOverviewDto getOverview() {
        long totalApps = applicationRepo.count();
        long pending = applicationRepo.countByStatus(ApplicationStatus.PENDING);
        long approved = applicationRepo.countByStatus(ApplicationStatus.APPROVED);
        long activeContracts = contractRepo.countByStatus(ContractStatus.ACTIVE);
        long totalOutstanding = contractRepo.sumRemainingByStatus(ContractStatus.ACTIVE);

        return DashboardOverviewDto.builder()
                .totalApplications(totalApps)
                .pendingApplications(pending)
                .approvedApplications(approved)
                .activeContracts(activeContracts)
                .totalOutstanding(totalOutstanding)
                .build();
    }
}
