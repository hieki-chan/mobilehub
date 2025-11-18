package org.mobilehub.installment_service.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import org.mobilehub.installment_service.domain.entity.Partner;
import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.repository.InstallmentApplicationRepository;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.repository.InstallmentPlanRepository;
import org.mobilehub.installment_service.repository.PartnerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PartnerRepository partnerRepo;
    private final InstallmentPlanRepository planRepo;
    private final InstallmentApplicationRepository appRepo;
    private final InstallmentContractRepository contractRepo;

    @Override
    public void run(String... args) {
        if (partnerRepo.count() > 0) return;

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
                        .partner(home)   // đã fix
                        .minPrice(3_000_000L)
                        .downPaymentPercent(0)
                        .interestRate(0.0)
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
                        .interestRate(1.8)
                        .allowedTenors("6,9,12")
                        .active(true)
                        .build()
        );

        // ======= APPLICATIONS =======
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
                        .status(ApplicationStatus.PENDING)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build()
        );

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
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .build()
        );

        // ======= CONTRACTS =======
        contractRepo.save(
                InstallmentContract.builder()
                        .code("CT-001")
                        .application(app2)
                        .plan(plan2)
                        .totalLoan(14_000_000L)
                        .remainingAmount(10_000_000L)
                        .status(ContractStatus.ACTIVE)
                        .startDate(LocalDate.now().minusMonths(2))
                        .endDate(LocalDate.now().plusMonths(10))
                        .createdAt(LocalDateTime.now().minusMonths(2))
                        .build()
        );
    }
}
