package org.mobilehub.installment_service.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import org.mobilehub.installment_service.domain.entity.Partner;
import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;
import org.mobilehub.installment_service.repository.InstallmentApplicationRepository;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.repository.InstallmentPaymentRepository;
import org.mobilehub.installment_service.repository.InstallmentPlanRepository;
import org.mobilehub.installment_service.repository.PartnerRepository;
import org.mobilehub.installment_service.util.InstallmentCalculator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PartnerRepository              partnerRepo;
    private final InstallmentPlanRepository      planRepo;
    private final InstallmentApplicationRepository appRepo;
    private final InstallmentContractRepository  contractRepo;
    private final InstallmentPaymentRepository   paymentRepo;

    @Override
    public void run(String... args) {
        // Nếu đã seed rồi thì thôi (tránh nhân đôi dữ liệu khi restart)
        if (partnerRepo.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Có thể coi như demo user/address cho tất cả hồ sơ seed
        final Long DEMO_USER_ID    = 1L;
        final Long DEMO_ADDRESS_ID = 1L;

        // ======= PARTNERS =======
        Partner fe = partnerRepo.save(
                Partner.builder()
                        .code("FE")
                        .name("FE Credit")
                        .contactPhone("0900000000")
                        .build()
        );

        Partner home = partnerRepo.save(
                Partner.builder()
                        .code("HOME")
                        .name("Home Credit")
                        .contactPhone("0123456789")
                        .build()
        );

        // ======= PLANS =======
        InstallmentPlan plan1 = planRepo.save(
                InstallmentPlan.builder()
                        .code("PLAN-001")
                        .name("0% qua thẻ tín dụng 6 tháng")
                        .partner(home)
                        .minPrice(3_000_000L)
                        .downPaymentPercent(0)
                        .interestRate(0.0)         // 0% lãi
                        .allowedTenors("3,6")
                        .active(true)
                        .build()
        );

        InstallmentPlan plan2 = planRepo.save(
                InstallmentPlan.builder()
                        .code("PLAN-002")
                        .name("Trả góp FE Credit 12 tháng")
                        .partner(fe)
                        .minPrice(5_000_000L)
                        .downPaymentPercent(20)
                        .interestRate(1.8)         // 1.8% / tháng
                        .allowedTenors("6,9,12")
                        .active(true)
                        .build()
        );

        // ======= TENOR DEFAULT THEO PLAN =======
        final int TENOR_PLAN1 = 6;
        final int TENOR_PLAN2 = 12;

        // ======= APPLICATIONS (Hồ sơ) =======
        // APP-001: iPhone 15, Home Credit, gói 0% 6 tháng — ĐÃ DUYỆT
        InstallmentApplication app1 = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-001")
                        .customerName("Nguyễn Văn A")
                        .customerPhone("0901234567")
                        .productName("iPhone 15")
                        .productPrice(25_000_000L)
                        .loanAmount(20_000_000L)
                        .partner(home)
                        .plan(plan1)
                        .tenorMonths(TENOR_PLAN1)
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(now.minusDays(3))

                        // ====== NEW FIELDS TMĐT ======
                        .userId(DEMO_USER_ID)
                        .productId(1001L)
                        .variantId(100101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        // =============================
                        .build()
        );

        // APP-002: Samsung S24, FE Credit 12 tháng — ĐÃ DUYỆT
        InstallmentApplication app2 = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-002")
                        .customerName("Trần Thị B")
                        .customerPhone("0909999999")
                        .productName("Samsung Galaxy S24")
                        .productPrice(18_000_000L)
                        .loanAmount(14_000_000L)
                        .partner(fe)
                        .plan(plan2)
                        .tenorMonths(TENOR_PLAN2)
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(now.minusDays(5))

                        .userId(DEMO_USER_ID)
                        .productId(2001L)
                        .variantId(200101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        // APP-003: hồ sơ đang chờ duyệt (để test trạng thái)
        InstallmentApplication app3Pending = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-003")
                        .customerName("Lê Văn C")
                        .customerPhone("0911222333")
                        .productName("Laptop Dell X")
                        .productPrice(30_000_000L)
                        .loanAmount(24_000_000L)
                        .partner(fe)
                        .plan(plan2)
                        .tenorMonths(TENOR_PLAN2)
                        .status(ApplicationStatus.PENDING)
                        .createdAt(now.minusDays(1))

                        .userId(DEMO_USER_ID)
                        .productId(3001L)
                        .variantId(300101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        // === Thêm một số hồ sơ APPROVED cho các tháng khác nhau
        InstallmentApplication app4 = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-004")
                        .customerName("Phạm Thị D")
                        .customerPhone("0908888888")
                        .productName("TV Sony 55\"")
                        .productPrice(18_000_000L)
                        .loanAmount(15_000_000L)
                        .partner(home)
                        .plan(plan2)
                        .tenorMonths(TENOR_PLAN2)
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(now.minusMonths(5).plusDays(2))   // T6

                        .userId(DEMO_USER_ID)
                        .productId(4001L)
                        .variantId(400101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        InstallmentApplication app5 = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-005")
                        .customerName("Ngô Văn E")
                        .customerPhone("0912333444")
                        .productName("Máy giặt LG")
                        .productPrice(12_000_000L)
                        .loanAmount(9_000_000L)
                        .partner(fe)
                        .plan(plan1)
                        .tenorMonths(TENOR_PLAN1)
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(now.minusMonths(3).plusDays(3))   // T8

                        .userId(DEMO_USER_ID)
                        .productId(5001L)
                        .variantId(500101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        InstallmentApplication app6 = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-006")
                        .customerName("Trần Thị F")
                        .customerPhone("0933555777")
                        .productName("Tủ lạnh Samsung")
                        .productPrice(20_000_000L)
                        .loanAmount(16_000_000L)
                        .partner(home)
                        .plan(plan2)
                        .tenorMonths(TENOR_PLAN2)
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(now.minusMonths(2).plusDays(1))   // T9

                        .userId(DEMO_USER_ID)
                        .productId(6001L)
                        .variantId(600101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        InstallmentApplication app7 = appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-007")
                        .customerName("Lê Minh G")
                        .customerPhone("0988111222")
                        .productName("MacBook Air M2")
                        .productPrice(30_000_000L)
                        .loanAmount(25_000_000L)
                        .partner(fe)
                        .plan(plan1)
                        .tenorMonths(TENOR_PLAN1)
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(now.minusMonths(1).plusDays(4))   // T10

                        .userId(DEMO_USER_ID)
                        .productId(7001L)
                        .variantId(700101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        // === Thêm vài hồ sơ PENDING để bảng "Hồ sơ chờ duyệt gần nhất" có dữ liệu
        appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-008")
                        .customerName("Đỗ Thị H")
                        .customerPhone("0907666555")
                        .productName("Điện thoại Oppo X")
                        .productPrice(10_000_000L)
                        .loanAmount(8_000_000L)
                        .partner(home)
                        .plan(plan1)
                        .tenorMonths(TENOR_PLAN1)
                        .status(ApplicationStatus.PENDING)
                        .createdAt(now.minusHours(6))

                        .userId(DEMO_USER_ID)
                        .productId(8001L)
                        .variantId(800101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-009")
                        .customerName("Phan Văn I")
                        .customerPhone("0905777333")
                        .productName("Tablet Android")
                        .productPrice(8_000_000L)
                        .loanAmount(6_000_000L)
                        .partner(fe)
                        .plan(plan2)
                        .tenorMonths(TENOR_PLAN2)
                        .status(ApplicationStatus.PENDING)
                        .createdAt(now.minusHours(3))

                        .userId(DEMO_USER_ID)
                        .productId(9001L)
                        .variantId(900101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-010")
                        .customerName("Nguyễn Thị K")
                        .customerPhone("0914111222")
                        .productName("Smart TV LG 65\"")
                        .productPrice(22_000_000L)
                        .loanAmount(18_000_000L)
                        .partner(fe)
                        .plan(plan2)
                        .tenorMonths(TENOR_PLAN2)
                        .status(ApplicationStatus.PENDING)
                        .createdAt(now.minusHours(1))

                        .userId(DEMO_USER_ID)
                        .productId(10001L)
                        .variantId(1000101L)
                        .quantity(1)
                        .addressId(DEMO_ADDRESS_ID)
                        .build()
        );

        // ======= CONTRACTS (Hợp đồng) =======
        // HĐ 1: CT-001 cho APP-002 (FE Credit 12 tháng) – START T7 (now - 4 tháng)
        LocalDate ct1StartDate = LocalDate.now().minusMonths(4);
        InstallmentContract ct1 = contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-001")
                        .application(app2)
                        .plan(plan2)
                        .totalLoan(app2.getLoanAmount())     // 14.000.000
                        .remainingAmount(10_000_000L)
                        .status(ContractStatus.ACTIVE)
                        .startDate(ct1StartDate)
                        .endDate(ct1StartDate.plusMonths(app2.getTenorMonths())) // ✅ theo tenor
                        .createdAt(ct1StartDate.atStartOfDay())
                        .build()
        );

        // HĐ 2: CT-002 cho APP-001 (0% 6 tháng) – START T11 (tháng hiện tại)
        LocalDate ct2StartDate = LocalDate.now();
        InstallmentContract ct2 = contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-002")
                        .application(app1)
                        .plan(plan1)
                        .totalLoan(app1.getLoanAmount())     // 20.000.000
                        .remainingAmount(app1.getLoanAmount())
                        .status(ContractStatus.ACTIVE)
                        .startDate(ct2StartDate)
                        .endDate(ct2StartDate.plusMonths(app1.getTenorMonths())) // ✅ theo tenor
                        .createdAt(now)
                        .build()
        );

        // HĐ 3: CT-003 cho APP-004 – START T6 (now - 5 tháng)
        LocalDate ct3StartDate = LocalDate.now().minusMonths(5);
        InstallmentContract ct3 = contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-003")
                        .application(app4)
                        .plan(plan2)
                        .totalLoan(app4.getLoanAmount())     // 15.000.000
                        .remainingAmount(12_000_000L)
                        .status(ContractStatus.ACTIVE)
                        .startDate(ct3StartDate)
                        .endDate(ct3StartDate.plusMonths(app4.getTenorMonths())) // ✅ theo tenor
                        .createdAt(ct3StartDate.atStartOfDay())
                        .build()
        );

        // HĐ 4: CT-004 cho APP-005 – START T8 (now - 3 tháng)
        LocalDate ct4StartDate = LocalDate.now().minusMonths(3);
        InstallmentContract ct4 = contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-004")
                        .application(app5)
                        .plan(plan1)
                        .totalLoan(app5.getLoanAmount())     // 9.000.000
                        .remainingAmount(9_000_000L)
                        .status(ContractStatus.ACTIVE)
                        .startDate(ct4StartDate)
                        .endDate(ct4StartDate.plusMonths(app5.getTenorMonths())) // ✅ theo tenor
                        .createdAt(ct4StartDate.atStartOfDay())
                        .build()
        );

        // HĐ 5: CT-005 cho APP-006 – START T9 (now - 2 tháng)
        LocalDate ct5StartDate = LocalDate.now().minusMonths(2);
        InstallmentContract ct5 = contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-005")
                        .application(app6)
                        .plan(plan2)
                        .totalLoan(app6.getLoanAmount())     // 16.000.000
                        .remainingAmount(13_000_000L)
                        .status(ContractStatus.ACTIVE)
                        .startDate(ct5StartDate)
                        .endDate(ct5StartDate.plusMonths(app6.getTenorMonths())) // ✅ theo tenor
                        .createdAt(ct5StartDate.atStartOfDay())
                        .build()
        );

        // HĐ 6: CT-006 cho APP-007 – START T10 (now - 1 tháng)
        LocalDate ct6StartDate = LocalDate.now().minusMonths(1);
        InstallmentContract ct6 = contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-006")
                        .application(app7)
                        .plan(plan1)
                        .totalLoan(app7.getLoanAmount())     // 25.000.000
                        .remainingAmount(25_000_000L)
                        .status(ContractStatus.ACTIVE)
                        .startDate(ct6StartDate)
                        .endDate(ct6StartDate.plusMonths(app7.getTenorMonths())) // ✅ theo tenor
                        .createdAt(ct6StartDate.atStartOfDay())
                        .build()
        );

        // ======= PAYMENT SCHEDULE (Lịch thanh toán) =======
        // CT-001 & CT-003 & CT-005: plan2 (annuity)
        seedScheduleForStandardContract(ct1, plan2, app2.getTenorMonths());
        seedScheduleForStandardContract(ct3, plan2, app4.getTenorMonths());
        seedScheduleForStandardContract(ct5, plan2, app6.getTenorMonths());

        // CT-002 & CT-004 & CT-006: plan1 (0% chia đều)
        seedScheduleForZeroInterestContract(ct2, plan1, app1.getTenorMonths());
        seedScheduleForZeroInterestContract(ct4, plan1, app5.getTenorMonths());
        seedScheduleForZeroInterestContract(ct6, plan1, app7.getTenorMonths());
    }

    /**
     * Lịch 0% lãi: chia đều gốc cho tenorMonths, tất cả trạng thái PLANNED.
     */
    private void seedScheduleForZeroInterestContract(InstallmentContract contract,
                                                     InstallmentPlan plan,
                                                     int tenorMonths) {

        long monthlyPayment = InstallmentCalculator.calculateAnnuityMonthlyPayment(
                contract.getTotalLoan(),
                plan.getInterestRate(),
                tenorMonths
        );

        List<InstallmentPayment> payments = new ArrayList<>();

        for (int i = 1; i <= tenorMonths; i++) {
            InstallmentPayment p = InstallmentPayment.builder()
                    .contract(contract)
                    .periodNumber(i)
                    .dueDate(contract.getStartDate().plusMonths(i)) // kỳ 1 sau ngày start 1 tháng
                    .amount(monthlyPayment)
                    .principalAmount(monthlyPayment)
                    .interestAmount(0L)
                    .status(PaymentStatus.PLANNED)
                    .build();

            payments.add(p);
        }

        paymentRepo.saveAll(payments);
    }

    /**
     * Lịch có lãi (annuity):
     * - Một số kỳ Paid/Overdue để dashboard có đủ case.
     */
    private void seedScheduleForStandardContract(InstallmentContract contract,
                                                 InstallmentPlan plan,
                                                 int tenorMonths) {

        double r = plan.getInterestRate() / 100.0;
        long monthlyPayment = InstallmentCalculator.calculateAnnuityMonthlyPayment(
                contract.getTotalLoan(),
                plan.getInterestRate(),
                tenorMonths
        );
        double remaining = contract.getTotalLoan();

        List<InstallmentPayment> payments = new ArrayList<>();

        for (int i = 1; i <= tenorMonths; i++) {
            long interest = Math.round(remaining * r);
            long principal = monthlyPayment - interest;

            // Kỳ cuối: chỉnh lại principal cho khớp dư nợ còn lại
            if (i == tenorMonths) {
                principal = Math.round(remaining);
                monthlyPayment = principal + interest;
            }

            PaymentStatus status;
            LocalDate paidDate = null;

            if (i <= 2) {
                status = PaymentStatus.PAID;
                paidDate = contract.getStartDate().plusMonths(i).minusDays(3);
            } else if (i == 3) {
                status = PaymentStatus.OVERDUE;
            } else {
                status = PaymentStatus.PLANNED;
            }

            InstallmentPayment p = InstallmentPayment.builder()
                    .contract(contract)
                    .periodNumber(i)
                    .dueDate(contract.getStartDate().plusMonths(i))
                    .amount(monthlyPayment)
                    .principalAmount(principal)
                    .interestAmount(interest)
                    .status(status)
                    .paidDate(paidDate)
                    .build();

            payments.add(p);
            remaining -= principal;
        }

        paymentRepo.saveAll(payments);
    }
}
