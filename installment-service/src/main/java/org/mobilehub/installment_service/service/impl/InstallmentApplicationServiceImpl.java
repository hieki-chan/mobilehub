package org.mobilehub.installment_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import org.mobilehub.installment_service.domain.entity.Partner;
import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;
import org.mobilehub.installment_service.dto.application.*;
import org.mobilehub.installment_service.repository.*;
import org.mobilehub.installment_service.service.InstallmentApplicationService;
import org.mobilehub.installment_service.util.InstallmentCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// [NEW]
import org.mobilehub.installment_service.messaging.InstallmentOrderCreateMessage;
import org.mobilehub.installment_service.messaging.InstallmentOrderMessagePublisher;
import org.mobilehub.installment_service.messaging.NotificationEventPublisher;
import org.mobilehub.installment_service.client.IdentityServiceClient;
import org.mobilehub.shared.contracts.notification.InstallmentApprovedEvent;

@Service
@RequiredArgsConstructor
public class InstallmentApplicationServiceImpl implements InstallmentApplicationService {

    private final InstallmentApplicationRepository appRepo;
    private final InstallmentContractRepository    contractRepo;
    private final InstallmentPaymentRepository     paymentRepo;

    private final InstallmentPlanRepository planRepo;
    private final PartnerRepository         partnerRepo;

    // [NEW] publisher Kafka để yêu cầu order-service tạo đơn
    private final InstallmentOrderMessagePublisher orderMessagePublisher;
    
    // [NEW] publisher Kafka để gửi thông báo
    private final NotificationEventPublisher notificationPublisher;
    
    // [NEW] client để lấy thông tin user từ identity-service
    private final IdentityServiceClient identityServiceClient;

    // ============================================================
    // SEARCH
    // ============================================================
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

    // ============================================================
    // UPDATE STATUS
    // ============================================================
    @Override
    @Transactional
    public void updateStatus(Long id, ApplicationStatusUpdateRequest request) {
        InstallmentApplication app = appRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        ApplicationStatus newStatus = request.getStatus();
        app.setStatus(newStatus);
        appRepo.save(app);

        // ✅ Nếu duyệt thì tạo hợp đồng + lịch thanh toán + bắn event tạo Order + gửi thông báo
        if (newStatus == ApplicationStatus.APPROVED) {
            createContractAndScheduleIfNotExist(app);
            publishOrderCreateEvent(app);   // [NEW]
            publishInstallmentApprovedNotification(app);  // [NEW] gửi thông báo
        }
    }

    // ============================================================
    // [NEW] PUBLISH EVENT TẠO ORDER QUA KAFKA
    // ============================================================
    private void publishOrderCreateEvent(InstallmentApplication app) {
        // build item từ thông tin application
        InstallmentOrderCreateMessage.Item item =
                InstallmentOrderCreateMessage.Item.builder()
                        .productId(app.getProductId())
                        .variantId(app.getVariantId())
                        .quantity(app.getQuantity())
                        .build();

        InstallmentOrderCreateMessage msg =
                InstallmentOrderCreateMessage.builder()
                        .applicationId(app.getId())
                        .userId(app.getUserId())
                        .paymentMethod("INSTALLMENT")     // map sang PaymentMethod.INSTALLMENT bên order-service
                        .shippingMethod("DELIVERY")       // tuỳ enum ShippingMethod bên order-service
                        .note("Order created from installment application " + app.getCode())
                        .addressId(app.getAddressId())
                        .items(List.of(item))
                        .build();

        orderMessagePublisher.publishInstallmentOrderCreate(msg);
    }

    // ============================================================
    // [NEW] PUBLISH NOTIFICATION EVENT KHI HỒ SƠ ĐƯỢC DUYỆT
    // ============================================================
    private void publishInstallmentApprovedNotification(InstallmentApplication app) {
        // Lấy email từ identity-service
        String userEmail = identityServiceClient.getUserEmail(app.getUserId());
        
        InstallmentApprovedEvent event = new InstallmentApprovedEvent(
                app.getId(),
                app.getUserId().toString(),
                app.getPlan().getName(),
                app.getTenorMonths(),
                userEmail
        );
        
        notificationPublisher.publishInstallmentApproved(event);
    }

    // ============================================================
    // AUTO CREATE CONTRACT + PAYMENTS
    // ============================================================
    private void createContractAndScheduleIfNotExist(InstallmentApplication app) {

        if (contractRepo.existsByApplicationId(app.getId())) return;

        Integer tenorMonths = app.getTenorMonths();
        if (tenorMonths == null || tenorMonths <= 0) {
            throw new IllegalArgumentException("tenorMonths không hợp lệ trong Application");
        }

        InstallmentPlan plan = app.getPlan();
        validateTenorAllowed(plan, tenorMonths);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate   = startDate.plusMonths(tenorMonths);

        InstallmentContract contract = InstallmentContract.builder()
                .code(generateContractCode())
                .application(app)
                .plan(plan)
                .totalLoan(app.getLoanAmount())
                .remainingAmount(app.getLoanAmount())
                .status(ContractStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate) // ✅ lấy theo tenor thật
                .createdAt(LocalDateTime.now())
                .build();

        contract = contractRepo.save(contract);

        // ✅ tạo các kỳ thanh toán
        List<InstallmentPayment> schedule = buildPaymentSchedule(
                contract,
                app.getLoanAmount(),
                plan.getInterestRate(),
                tenorMonths,
                startDate
        );
        paymentRepo.saveAll(schedule);
    }

    private List<InstallmentPayment> buildPaymentSchedule(
            InstallmentContract contract,
            long loanAmount,
            double monthlyInterestRatePercent,
            int tenorMonths,
            LocalDate startDate
    ) {
        List<InstallmentPayment> result = new ArrayList<>(tenorMonths);

        long monthlyPayment = InstallmentCalculator.calculateAnnuityMonthlyPayment(
                loanAmount,
                monthlyInterestRatePercent,
                tenorMonths
        );

        double r = monthlyInterestRatePercent / 100.0;
        long remainingPrincipal = loanAmount;

        for (int period = 1; period <= tenorMonths; period++) {

            long interest = Math.round(remainingPrincipal * r);
            long principal = monthlyPayment - interest;

            // kỳ cuối chỉnh cho hết nợ do làm tròn
            if (period == tenorMonths) {
                principal = remainingPrincipal;
                monthlyPayment = principal + interest;
            }

            remainingPrincipal -= principal;

            InstallmentPayment p = InstallmentPayment.builder()
                    .contract(contract)
                    .periodNumber(period)
                    .dueDate(startDate.plusMonths(period)) // kỳ 1 sau 1 tháng
                    .principalAmount(principal)
                    .amount(monthlyPayment)               // gốc + lãi
                    .status(PaymentStatus.PLANNED)
                    .build();

            result.add(p);
        }

        return result;
    }

    private void validateTenorAllowed(InstallmentPlan plan, Integer tenorMonths) {
        String allowed = plan.getAllowedTenors();
        if (!StringUtils.hasText(allowed)) return;

        Set<Integer> allowedSet = Arrays.stream(allowed.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        if (!allowedSet.contains(tenorMonths)) {
            throw new IllegalArgumentException(
                    "Tenor " + tenorMonths + " không nằm trong allowedTenors của plan: " + allowed
            );
        }
    }

    private String generateContractCode() {
        long count = contractRepo.count() + 1;
        return String.format("CT-%03d", count);
    }

    // ============================================================
    // PRECHECK
    // ============================================================
    @Override
    public ApplicationPrecheckResponse precheck(ApplicationPrecheckRequest req) {
        // giữ nguyên như bạn đã có
        // ...
        InstallmentPlan plan = planRepo.findById(req.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Partner partner = partnerRepo.findById(plan.getPartner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        if (req.getProductPrice() < plan.getMinPrice()) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Giá sản phẩm nhỏ hơn mức tối thiểu của gói trả góp")
                    .build();
        }

        long minDownPayment = req.getProductPrice() * plan.getDownPaymentPercent() / 100;
        long maxLoanAmount  = req.getProductPrice() - minDownPayment;

        if (req.getLoanAmount() > maxLoanAmount) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Số tiền vay vượt quá mức cho phép với gói này")
                    .build();
        }

        Integer tenor = req.getTenorMonths();
        if (tenor == null || tenor <= 0) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Kỳ hạn trả góp (tenorMonths) không hợp lệ")
                    .build();
        }

        validateTenorAllowed(plan, tenor);

        long monthlyPayment = InstallmentCalculator.calculateAnnuityMonthlyPayment(
                req.getLoanAmount(),
                plan.getInterestRate(),
                tenor
        );

        if (monthlyPayment > req.getMonthlyIncome() * 0.4) {
            return ApplicationPrecheckResponse.builder()
                    .eligible(false)
                    .message("Số tiền phải trả mỗi tháng (" + monthlyPayment
                            + ") vượt quá 40% thu nhập của khách hàng")
                    .build();
        }

        return ApplicationPrecheckResponse.builder()
                .eligible(true)
                .message("Khách hàng đủ điều kiện. Dự kiến trả mỗi tháng: " + monthlyPayment)
                .build();
    }

    // ============================================================
    // CREATE APPLICATION
    // ============================================================
    @Override
    @Transactional
    public ApplicationResponse create(ApplicationCreateRequest req) {

        InstallmentPlan plan = planRepo.findById(req.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        Partner partner = partnerRepo.findById(plan.getPartner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        Integer tenor = req.getTenorMonths();
        if (tenor == null || tenor <= 0) {
            throw new IllegalArgumentException("tenorMonths không hợp lệ");
        }
        validateTenorAllowed(plan, tenor);

        InstallmentApplication app = InstallmentApplication.builder()
                .code(generateNextCode())
                .customerName(req.getCustomerName())
                .customerPhone(req.getCustomerPhone())
                .productName(req.getProductName())
                .productPrice(req.getProductPrice())
                .loanAmount(req.getLoanAmount())
                .partner(partner)
                .plan(plan)
                .tenorMonths(tenor)
                .status(ApplicationStatus.APPROVED)  // ✅ TỰ ĐỘNG DUYỆT
                .createdAt(LocalDateTime.now())

                // [NEW] set các field TMĐT
                .userId(req.getUserId())
                .productId(req.getProductId())
                .variantId(req.getVariantId())
                .quantity(req.getQuantity())
                .addressId(req.getAddressId())
                .build();

        app = appRepo.save(app);
        
        // ✅ TỰ ĐỘNG tạo hợp đồng + lịch thanh toán + gửi event
        createContractAndScheduleIfNotExist(app);
        publishOrderCreateEvent(app);
        publishInstallmentApprovedNotification(app);
        
        return toResponse(app);
    }

    private String generateNextCode() {
        long count = appRepo.count() + 1;
        return String.format("APP-%03d", count);
    }

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
