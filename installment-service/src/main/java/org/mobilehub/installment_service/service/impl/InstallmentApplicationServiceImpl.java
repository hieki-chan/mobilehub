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
import org.mobilehub.installment_service.repository.InstallmentPlanRepository;
import org.mobilehub.installment_service.repository.PartnerRepository;
import org.mobilehub.installment_service.service.InstallmentApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import org.mobilehub.installment_service.domain.entity.Partner;
import org.mobilehub.installment_service.dto.application.ApplicationCreateRequest;
import org.mobilehub.installment_service.dto.application.ApplicationPrecheckRequest;
import org.mobilehub.installment_service.dto.application.ApplicationPrecheckResponse;
import org.mobilehub.installment_service.util.InstallmentCalculator;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentApplicationServiceImpl implements InstallmentApplicationService {

    private final InstallmentApplicationRepository appRepo;
    private final InstallmentContractRepository contractRepo;

    private final InstallmentPlanRepository planRepo;
    private final PartnerRepository partnerRepo;

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

        //  Nếu duỵệt thì tạo hợp đồng
        if (request.getStatus().name().equals("APPROVED")) {
            createContractIfNotExist(app);
        }
    }

    // ============================================================
    // Tự động tạo hợp đồng khi duyệt hồ sơ
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

    @Override
    public ApplicationPrecheckResponse precheck(ApplicationPrecheckRequest req) {

        // 1. Kiểm tra plan & partner có tồn tại không
        InstallmentPlan plan = planRepo.findById(req.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Partner partner = partnerRepo.findById(req.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        // 2. Rule 1: giá sản phẩm phải >= minPrice của plan
        if (req.getProductPrice() < plan.getMinPrice()) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Giá sản phẩm nhỏ hơn mức tối thiểu của gói trả góp")
                    .build();
        }

        // 3. Rule 2: số tiền vay không vượt quá (giá - tiền trả trước tối thiểu)
        long minDownPayment = req.getProductPrice() * plan.getDownPaymentPercent() / 100;
        long maxLoanAmount = req.getProductPrice() - minDownPayment;

        if (req.getLoanAmount() > maxLoanAmount) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Số tiền vay vượt quá mức cho phép với gói này")
                    .build();
        }

        // 4. Rule 3: dùng công thức annuity để tính tiền trả hàng tháng
        Integer tenor = req.getTenorMonths(); // field này bạn thêm trong DTO
        if (tenor == null || tenor <= 0) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Kỳ hạn trả góp (tenorMonths) không hợp lệ")
                    .build();
        }

        long monthlyPayment = InstallmentCalculator.calculateAnnuityMonthlyPayment(
                req.getLoanAmount(),
                plan.getInterestRate(), // interestRate đang là %/tháng trong InstallmentPlan
                tenor
        );

        // Ví dụ: không cho tiền trả góp > 40% thu nhập
        if (monthlyPayment > req.getMonthlyIncome() * 0.4) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Số tiền phải trả mỗi tháng (" + monthlyPayment
                            + ") vượt quá 40% thu nhập của khách hàng")
                    .build();
        }

        // Nếu tất cả rule pass → đủ điều kiện
        return ApplicationPrecheckResponse.builder()
                .eligible(true)
                .message("Khách hàng đủ điều kiện. Dự kiến trả mỗi tháng: " + monthlyPayment)
                .build();
    }


    @Override
    @Transactional
    public ApplicationResponse create(ApplicationCreateRequest req) {

        InstallmentPlan plan = planRepo.findById(req.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Partner partner = partnerRepo.findById(req.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        // (Optional) gọi lại precheck để đảm bảo backend vẫn kiểm tra
        // ApplicationPrecheckResponse precheck = precheck(convert(req));
        // if (!precheck.isEligible()) throw new IllegalStateException("Not eligible");

        InstallmentApplication app = InstallmentApplication.builder()
                .code(generateNextCode())           // APP-003, APP-004...
                .customerName(req.getCustomerName())
                .customerPhone(req.getCustomerPhone())
                .productName(req.getProductName())
                .productPrice(req.getProductPrice())
                .loanAmount(req.getLoanAmount())
                .partner(partner)
                .plan(plan)
                .status(ApplicationStatus.PENDING)  // luôn bắt đầu từ PENDING
                .createdAt(java.time.LocalDateTime.now())
                .build();

        app = appRepo.save(app);
        return toResponse(app);   // dùng method private đã có sẵn ở cuối file
    }

    private String generateNextCode() {
        long count = appRepo.count() + 1;  // đơn giản: số lượng bản ghi + 1
        return String.format("APP-%03d", count);   // APP-001, APP-002, APP-003...
    }

}
