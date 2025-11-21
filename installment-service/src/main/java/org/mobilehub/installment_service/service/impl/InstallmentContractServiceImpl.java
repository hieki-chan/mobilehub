package org.mobilehub.installment_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;
import org.mobilehub.installment_service.dto.contract.ContractDetailResponse;
import org.mobilehub.installment_service.dto.contract.ContractFilter;
import org.mobilehub.installment_service.dto.contract.ContractResponse;
import org.mobilehub.installment_service.dto.contract.PaymentScheduleItemResponse;
import org.mobilehub.installment_service.repository.InstallmentContractRepository;
import org.mobilehub.installment_service.repository.InstallmentPaymentRepository;
import org.mobilehub.installment_service.service.InstallmentContractService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentContractServiceImpl implements InstallmentContractService {

    /**
     * Phạt chậm trả 2% mỗi tháng trên số tiền kỳ bị trễ.
     */
    private static final double LATE_PENALTY_RATE_PER_MONTH = 2.0d;

    private final InstallmentContractRepository contractRepo;
    private final InstallmentPaymentRepository  paymentRepo;

    // =========================================================
    // SEARCH
    // =========================================================
    @Override
    public List<ContractResponse> searchContracts(ContractFilter filter) {
        List<InstallmentContract> contracts = contractRepo.findAll();

        return contracts.stream()
                .filter(ct -> filter.getStatus() == null || ct.getStatus() == filter.getStatus())
                .filter(ct -> {
                    String q = filter.getQ();
                    if (!StringUtils.hasText(q)) return true;
                    q = q.toLowerCase();

                    InstallmentApplication app = ct.getApplication();

                    return (ct.getCode() != null && ct.getCode().toLowerCase().contains(q))
                            || (app != null && app.getCode() != null
                            && app.getCode().toLowerCase().contains(q))
                            || (app != null && app.getCustomerName() != null
                            && app.getCustomerName().toLowerCase().contains(q))
                            || (app != null && app.getProductName() != null
                            && app.getProductName().toLowerCase().contains(q));
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // DETAIL + LỊCH THANH TOÁN
    // =========================================================
    @Override
    public ContractDetailResponse getContractDetail(Long id) {
        InstallmentContract ct = contractRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id = " + id));

        InstallmentApplication app  = ct.getApplication();
        InstallmentPlan        plan = ct.getPlan();

        List<InstallmentPayment> payments =
                paymentRepo.findByContractIdOrderByPeriodNumberAsc(ct.getId());

        // ===== FIX BUG: hợp đồng chưa có payment KHÔNG được coi là đã tất toán =====
        if (payments.isEmpty()) {

            // Nếu lỡ bị set CLOSED từ logic cũ thì trả về ACTIVE
            if (ct.getStatus() == null || ct.getStatus() == ContractStatus.CLOSED) {
                ct.setStatus(ContractStatus.ACTIVE);
                contractRepo.save(ct);
            }

            long remaining = fallbackRemainingAmount(ct);

            return ContractDetailResponse.builder()
                    .id(ct.getId())
                    .code(ct.getCode())
                    .applicationCode(app != null ? app.getCode() : null)
                    .customerName(app != null ? app.getCustomerName() : null)
                    .customerPhone(app != null ? app.getCustomerPhone() : null)
                    .productName(app != null ? app.getProductName() : null)
                    .planName(plan != null ? plan.getName() : null)
                    .totalLoan(ct.getTotalLoan())
                    .remainingAmount(remaining)
                    .status(ct.getStatus())
                    .startDate(ct.getStartDate())
                    .endDate(ct.getEndDate())
                    .totalPeriods(0)
                    .paidPeriods(0)
                    .schedule(List.of())
                    .build();
        }
        // =======================================================================

        LocalDate today          = LocalDate.now();
        boolean   paymentChanged = false;

        // 1. Đánh dấu các kỳ QUÁ HẠN
        for (InstallmentPayment p : payments) {
            if (p.getStatus() != PaymentStatus.PAID
                    && today.isAfter(p.getDueDate())
                    && p.getStatus() != PaymentStatus.OVERDUE) {

                p.setStatus(PaymentStatus.OVERDUE);
                paymentChanged = true;
            }
        }
        if (paymentChanged) {
            paymentRepo.saveAll(payments);
        }

        // 2. Thống kê kỳ + dư nợ (chỉ tính phần GỐC chưa trả)
        int totalPeriods = payments.size();
        int paidPeriods  = (int) payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .count();

        long remainingAmount = payments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)   // OVERDUE + PLANNED
                .mapToLong(InstallmentPayment::getPrincipalAmount)
                .sum();

        // 3. Xác định kỳ "gánh nợ"
        InstallmentPayment collectionPayment = payments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID
                        && !p.getDueDate().isBefore(today))         // dueDate >= today
                .min(Comparator
                        .comparing(InstallmentPayment::getDueDate)
                        .thenComparingInt(InstallmentPayment::getPeriodNumber))
                .orElse(null);

        // Nếu không có kỳ nào có dueDate >= today, chọn kỳ chưa trả sớm nhất
        if (collectionPayment == null) {
            collectionPayment = payments.stream()
                    .filter(p -> p.getStatus() != PaymentStatus.PAID)
                    .min(Comparator
                            .comparing(InstallmentPayment::getDueDate)
                            .thenComparingInt(InstallmentPayment::getPeriodNumber))
                    .orElse(null);
        }

        long overdueSum = 0L;   // tổng tiền (gốc + lãi) của các kỳ quá hạn trước kỳ gánh nợ
        long penaltySum = 0L;   // tổng tiền phạt tương ứng

        if (collectionPayment != null) {
            for (InstallmentPayment p : payments) {
                // chỉ cộng các kỳ QUÁ HẠN trước kỳ gánh nợ
                if (p.getStatus() == PaymentStatus.OVERDUE
                        && p.getDueDate().isBefore(collectionPayment.getDueDate())) {

                    overdueSum += p.getAmount();           // gốc + lãi kỳ quá hạn
                    penaltySum += calculatePenalty(p, today);
                }
            }
        }

        int  collectionPeriodNo = (collectionPayment != null) ? collectionPayment.getPeriodNumber() : -1;
        long extraAmount        = overdueSum + penaltySum;  // nợ quá hạn + phạt dồn lên kỳ gánh nợ

        // 4. Build schedule: amount = số tiền THỰC TẾ phải trả cho từng kỳ
        List<PaymentScheduleItemResponse> schedule = payments.stream()
                .map(p -> {
                    long baseAmount    = p.getAmount();              // gốc + lãi gốc
                    long principal     = p.getPrincipalAmount();     // gốc kỳ này
                    long baseInterest  = baseAmount - principal;     // lãi gốc
                    long displayAmount = baseAmount;                 // tiền phải trả hiển thị

                    // Kỳ gánh nợ: cộng thêm nợ quá hạn + phạt
                    if (p.getPeriodNumber() == collectionPeriodNo && extraAmount > 0) {
                        displayAmount += extraAmount;
                    }

                    return PaymentScheduleItemResponse.builder()
                            .period(p.getPeriodNumber())
                            .dueDate(p.getDueDate())
                            .principalAmount(principal)
                            .interestAmount(baseInterest)
                            .amount(displayAmount)
                            .status(p.getStatus())
                            .paidDate(p.getPaidDate())
                            .build();
                })
                .collect(Collectors.toList());

        // 5. Cập nhật trạng thái hợp đồng (ACTIVE / OVERDUE / CLOSED)
        boolean allPaid    = payments.stream().allMatch(p -> p.getStatus() == PaymentStatus.PAID);
        boolean anyOverdue = payments.stream().anyMatch(p -> p.getStatus() == PaymentStatus.OVERDUE);

        ContractStatus newStatus;
        if (allPaid) {
            newStatus = ContractStatus.CLOSED;
        } else if (anyOverdue) {
            newStatus = ContractStatus.OVERDUE;
        } else {
            newStatus = ContractStatus.ACTIVE;
        }

        if (ct.getStatus() != newStatus) {
            ct.setStatus(newStatus);
            contractRepo.save(ct);
        }

        return ContractDetailResponse.builder()
                .id(ct.getId())
                .code(ct.getCode())
                .applicationCode(app != null ? app.getCode() : null)
                .customerName(app != null ? app.getCustomerName() : null)
                .customerPhone(app != null ? app.getCustomerPhone() : null)
                .productName(app != null ? app.getProductName() : null)
                .planName(plan != null ? plan.getName() : null)
                .totalLoan(ct.getTotalLoan())
                .remainingAmount(remainingAmount)       // dư nợ = tổng GỐC chưa trả
                .status(ct.getStatus())
                .startDate(ct.getStartDate())
                .endDate(ct.getEndDate())
                .totalPeriods(totalPeriods)
                .paidPeriods(paidPeriods)
                .schedule(schedule)
                .build();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Dùng cho list để "Còn lại" khớp với chi tiết.
     * Nếu chưa có payment nào (contract mới), trả về remainingAmount/totalLoan gốc.
     */
    private long calculateRemainingAmount(InstallmentContract ct) {
        List<InstallmentPayment> payments =
                paymentRepo.findByContractIdOrderByPeriodNumberAsc(ct.getId());

        if (payments.isEmpty()) {
            return fallbackRemainingAmount(ct);
        }

        return payments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .mapToLong(InstallmentPayment::getPrincipalAmount)
                .sum();
    }

    private long fallbackRemainingAmount(InstallmentContract ct) {
        if (ct.getRemainingAmount() != null) {
            return ct.getRemainingAmount();
        }
        if (ct.getTotalLoan() != null) {
            return ct.getTotalLoan();
        }
        return 0L;
    }

    /**
     * Tính tiền phạt cho 1 kỳ quá hạn:
     *  - LATE_PENALTY_RATE_PER_MONTH % mỗi tháng
     *  - tính trên toàn bộ amount của kỳ đó
     *  - tối thiểu 1 tháng nếu đã quá hạn.
     */
    private long calculatePenalty(InstallmentPayment payment, LocalDate today) {
        if (today.isBefore(payment.getDueDate())) {
            return 0L;
        }

        long monthsOverdue = ChronoUnit.MONTHS.between(payment.getDueDate(), today);
        if (monthsOverdue <= 0) {
            monthsOverdue = 1;
        }

        double ratePerMonth = LATE_PENALTY_RATE_PER_MONTH / 100.0;
        double penalty      = payment.getAmount() * ratePerMonth * monthsOverdue;

        return Math.round(penalty);
    }

    private ContractResponse toResponse(InstallmentContract ct) {
        InstallmentApplication app  = ct.getApplication();
        InstallmentPlan        plan = ct.getPlan();

        long remainingAmount = calculateRemainingAmount(ct);

        return ContractResponse.builder()
                .id(ct.getId())
                .code(ct.getCode())
                .applicationCode(app != null ? app.getCode() : null)
                .customerName(app != null ? app.getCustomerName() : null)
                .productName(app != null ? app.getProductName() : null)
                .planName(plan != null ? plan.getName() : null)
                .totalLoan(ct.getTotalLoan())
                .remainingAmount(remainingAmount)
                .status(ct.getStatus())
                .build();
    }
}
