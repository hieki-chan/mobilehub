package org.mobilehub.installment_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;
import org.mobilehub.installment_service.dto.dashboard.DashboardOverviewDto;
import org.mobilehub.installment_service.dto.dashboard.MonthlyRevenueDto;
import org.mobilehub.installment_service.dto.dashboard.RecentApplicationDto;
import org.mobilehub.installment_service.repository.InstallmentApplicationRepository;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.repository.InstallmentPaymentRepository;
import org.mobilehub.installment_service.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final InstallmentApplicationRepository applicationRepo;
    private final InstallmentContractRepository    contractRepo;
    private final InstallmentPaymentRepository     paymentRepo;

    @Override
    public DashboardOverviewDto getOverview() {

        /* ================== 1. HỒ SƠ ================== */
        long totalApplications    = applicationRepo.count();
        long approvedApplications = applicationRepo.countByStatus(ApplicationStatus.APPROVED);
        long pendingApplications  = applicationRepo.countByStatus(ApplicationStatus.PENDING);
        long rejectedApplications = applicationRepo.countByStatus(ApplicationStatus.REJECTED);

        /* ================== 2. HỢP ĐỒNG & DƯ NỢ ================== */
        long activeContracts  = contractRepo.countByStatus(ContractStatus.ACTIVE);
        long overdueContracts = contractRepo.countByStatus(ContractStatus.OVERDUE);

        long totalRevenue = contractRepo.sumTotalLoan(); // Doanh số giải ngân

        // Dư nợ hiện tại = tổng GỐC chưa trả của các hợp đồng ACTIVE + OVERDUE
        long outstandingDebt = paymentRepo.findAll().stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .filter(p -> {
                    ContractStatus st = p.getContract().getStatus();
                    return st == ContractStatus.ACTIVE || st == ContractStatus.OVERDUE;
                })
                .mapToLong(InstallmentPayment::getPrincipalAmount)
                .sum();

        /* ================== 3. BIỂU ĐỒ DOANH THU 6 THÁNG ================== */

        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);          // tháng hiện tại
        YearMonth fromMonth = thisMonth.minusMonths(5);       // lùi 5 tháng => tổng 6 tháng

        LocalDateTime fromDate = fromMonth.atDay(1).atStartOfDay();

        List<InstallmentContract> recentContracts =
                contractRepo.findByCreatedAtAfter(fromDate);

        Map<YearMonth, Long> revenueByMonth = new HashMap<>();
        for (InstallmentContract ct : recentContracts) {
            if (ct.getCreatedAt() == null) continue;

            YearMonth ym = YearMonth.from(ct.getCreatedAt().toLocalDate());
            if (ym.isBefore(fromMonth) || ym.isAfter(thisMonth)) continue;

            revenueByMonth.merge(ym, ct.getTotalLoan(), Long::sum);
        }

        // xây list theo thứ tự thời gian từ cũ -> mới
        List<MonthlyRevenueDto> revenueChart = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth ym = fromMonth.plusMonths(i);
            long revenue = revenueByMonth.getOrDefault(ym, 0L);
            String label = "T" + ym.getMonthValue(); // T1, T2,...

            revenueChart.add(
                    MonthlyRevenueDto.builder()
                            .monthLabel(label)
                            .revenue(revenue)
                            .build()
            );
        }

        // % tăng/giảm doanh số tháng này vs tháng trước
        long currentMonthRevenue = revenueByMonth.getOrDefault(thisMonth, 0L);
        long prevMonthRevenue    = revenueByMonth.getOrDefault(thisMonth.minusMonths(1), 0L);

        double revenueGrowthPercent = 0.0;
        if (prevMonthRevenue > 0) {
            revenueGrowthPercent =
                    ((double) currentMonthRevenue - prevMonthRevenue)
                            / prevMonthRevenue * 100.0;
        }

        /* ================== 4. HỒ SƠ CHỜ DUYỆT GẦN NHẤT ================== */

        List<InstallmentApplication> recentPendingApps =
                applicationRepo.findTop5ByStatusOrderByCreatedAtDesc(ApplicationStatus.PENDING);

        List<RecentApplicationDto> recentPendingDtos = recentPendingApps.stream()
                .map(app -> RecentApplicationDto.builder()
                        .customerName(app.getCustomerName())
                        .productName(app.getProductName())
                        .planName(app.getPlan() != null ? app.getPlan().getName() : null)
                        .createdAt(app.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        /* ================== 5. BUILD DTO ================== */

        return DashboardOverviewDto.builder()
                .totalRevenue(totalRevenue)
                .outstandingDebt(outstandingDebt)
                .overdueContracts(overdueContracts)
                .activeContracts(activeContracts)

                .totalApplications(totalApplications)
                .approvedApplications(approvedApplications)
                .pendingApplications(pendingApplications)
                .rejectedApplications(rejectedApplications)

                .revenueGrowthPercent(revenueGrowthPercent)
                .revenueChart(revenueChart)
                .recentPendingApplications(recentPendingDtos)
                .build();
    }
}
