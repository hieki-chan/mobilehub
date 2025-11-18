package org.mobilehub.installment_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.dto.application.ApplicationFilter;
import org.mobilehub.installment_service.dto.application.ApplicationResponse;
import org.mobilehub.installment_service.dto.application.ApplicationStatusUpdateRequest;
import org.mobilehub.installment_service.repository.InstallmentApplicationRepository;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.service.InstallmentApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentApplicationServiceImpl implements InstallmentApplicationService {

    private final InstallmentApplicationRepository appRepo;
    private final InstallmentContractRepository contractRepo;

    @Override
    public List<ApplicationResponse> searchApplications(ApplicationFilter filter) {
        return appRepo.findAll().stream()
                .filter(app -> filter.getStatus() == null || app.getStatus() == filter.getStatus())
                .filter(app -> filter.getPartnerId() == null
                        || app.getPartner().getId().equals(filter.getPartnerId()))
                .filter(app -> {
                    if (!StringUtils.hasText(filter.getQ())) return true;
                    String q = filter.getQ().toLowerCase();
                    return app.getCode().toLowerCase().contains(q)
                            || app.getCustomerName().toLowerCase().contains(q)
                            || app.getProductName().toLowerCase().contains(q);
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ApplicationStatusUpdateRequest request) {
        InstallmentApplication app = appRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        app.setStatus(request.getStatus());
        appRepo.save(app);

        // 🔥 Nếu duỵệt thì tạo hợp đồng
        if (request.getStatus().name().equals("APPROVED")) {
            createContractIfNotExist(app);
        }
    }

    // ============================================================
    // 🔥 Tự động tạo hợp đồng khi duyệt hồ sơ
    // ============================================================
    private void createContractIfNotExist(InstallmentApplication app) {

        // Nếu đã có contract -> bỏ qua
        boolean exists = contractRepo.existsByApplicationId(app.getId());
        if (exists) return;

        InstallmentContract contract = InstallmentContract.builder()
                .code(generateContractCode())
                .application(app)
                .plan(app.getPlan())
                .totalLoan(app.getLoanAmount())
                .remainingAmount(app.getLoanAmount())
                .status(ContractStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .createdAt(LocalDateTime.now())
                .build();

        contractRepo.save(contract);
    }

    private String generateContractCode() {
        long count = contractRepo.count() + 1;
        return String.format("CT-%03d", count);
    }

    // ============================================================
    // Convert entity → response
    // ============================================================
    private ApplicationResponse toResponse(InstallmentApplication app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .code(app.getCode())
                .customerName(app.getCustomerName())
                .customerPhone(app.getCustomerPhone())
                .productName(app.getProductName())
                .productPrice(app.getProductPrice())
                .loanAmount(app.getLoanAmount())
                .partnerName(app.getPartner().getName())
                .planName(app.getPlan().getName())
                .status(app.getStatus())
                .build();
    }
}
