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

    private final PartnerRepository partnerRepo;
    private final InstallmentPlanRepository planRepo;
    private final InstallmentApplicationRepository appRepo;
    private final InstallmentContractRepository contractRepo;
    private final InstallmentPaymentRepository paymentRepo;

    @Override
    public void run(String... args) {
        // Nếu đã seed rồi thì thôi
        if (partnerRepo.count() > 0) {
            return;
        }

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
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(LocalDateTime.now().minusDays(3))
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
                        .status(ApplicationStatus.APPROVED)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build()
        );

        // APP-003: thêm 1 hồ sơ đang chờ duyệt để test filter
        appRepo.save(
                InstallmentApplication.builder()
                        .code("APP-003")
                        .customerName("Lê Văn C")
                        .customerPhone("0911222333")
                        .productName("Laptop Dell X")
                        .productPrice(30_000_000L)
                        .loanAmount(24_000_000L)
                        .partner(fe)
                        .plan(plan2)
                        .status(ApplicationStatus.PENDING)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build()
        );

        // ======= CONTRACTS (Hợp đồng) =======
        // HĐ 1: CT-001 cho APP-002 (FE Credit 12 tháng)
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
                        .endDate(ct1StartDate.plusMonths(12))
                        .createdAt(ct1StartDate.atStartOfDay())
                        .build()
        );

        // HĐ 2: CT-002 cho APP-001 (0% 6 tháng)
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
                        .endDate(ct2StartDate.plusMonths(6))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // ======= PAYMENT SCHEDULE (Lịch thanh toán) =======
        seedScheduleForStandardContract(ct1, plan2, 12); // annuity 12 kỳ, có Paid/Overdue
        seedScheduleForZeroInterestContract(ct2, plan1, 6); // 0% lãi, 6 kỳ Planned
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
     * Lịch có lãi (annuity): 12 kỳ.
     * Kỳ 1–2: PAID, kỳ 3: OVERDUE, còn lại: PLANNED.
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
