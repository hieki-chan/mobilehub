package org.mobilehub.installment_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
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

    // Phạt chậm trả 2% mỗi tháng trên số tiền kỳ bị trễ
    private static final double LATE_PENALTY_RATE_PER_MONTH = 2.0;

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
                    if (!StringUtils.hasText(q)) {
                        return true;
                    }
                    q = q.toLowerCase();

                    return (ct.getCode() != null && ct.getCode().toLowerCase().contains(q))
                            || (ct.getApplication() != null && ct.getApplication().getCode() != null
                            && ct.getApplication().getCode().toLowerCase().contains(q))
                            || (ct.getApplication() != null && ct.getApplication().getCustomerName() != null
                            && ct.getApplication().getCustomerName().toLowerCase().contains(q))
                            || (ct.getApplication() != null && ct.getApplication().getProductName() != null
                            && ct.getApplication().getProductName().toLowerCase().contains(q));
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

        List<InstallmentPayment> payments =
                paymentRepo.findByContractIdOrderByPeriodNumberAsc(ct.getId());

        LocalDate today = LocalDate.now();
        boolean paymentChanged = false;

        // 1. Đánh dấu các kỳ quá hạn
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
                .filter(p -> p.getStatus() != PaymentStatus.PAID)      // OVERDUE + PLANNED
                .mapToLong(InstallmentPayment::getPrincipalAmount)
                .sum();

        // 3. Xác định kỳ "gánh nợ"
        //    - Kỳ chưa trả có dueDate >= hôm nay gần nhất
        //    - Nếu không có (tất cả còn lại đều quá hạn) -> kỳ chưa trả sớm nhất
        InstallmentPayment collectionPayment = payments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID
                        && !p.getDueDate().isBefore(today)) // dueDate >= today
                .min(Comparator
                        .comparing(InstallmentPayment::getDueDate)
                        .thenComparingInt(InstallmentPayment::getPeriodNumber))
                .orElse(null);

        if (collectionPayment == null) {
            collectionPayment = payments.stream()
                    .filter(p -> p.getStatus() != PaymentStatus.PAID)
                    .min(Comparator
                            .comparing(InstallmentPayment::getDueDate)
                            .thenComparingInt(InstallmentPayment::getPeriodNumber))
                    .orElse(null);
        }

        long overdueSum = 0L;   // tổng amount của các kỳ quá hạn trước đó chưa trả
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

        int collectionPeriodNo = collectionPayment != null
                ? collectionPayment.getPeriodNumber()
                : -1;

        // Số tiền cộng thêm (nợ quá hạn + phạt) cho KỲ GÁNH NỢ
        final long extraAmount = overdueSum + penaltySum;

        // 4. Build schedule: amount = số tiền THỰC TẾ phải trả cho kỳ đó
        List<PaymentScheduleItemResponse> schedule = payments.stream()
                .map(p -> {
                    long baseAmount    = p.getAmount();              // tổng theo plan gốc (gốc + lãi)
                    long principal     = p.getPrincipalAmount();     // gốc kỳ này
                    long baseInterest  = baseAmount - principal;     // lãi kỳ này (theo plan)
                    long displayAmount = baseAmount;                 // số tiền thực tế phải trả

                    // Kỳ gánh nợ: cộng dồn nợ quá hạn + phạt
                    if (p.getPeriodNumber() == collectionPeriodNo && extraAmount > 0) {
                        displayAmount += extraAmount;
                    }

                    return PaymentScheduleItemResponse.builder()
                            .period(p.getPeriodNumber())
                            .dueDate(p.getDueDate())
                            .principalAmount(principal)     // gốc
                            .interestAmount(baseInterest)    // lãi (không gồm phạt)
                            .amount(displayAmount)           // tổng phải trả (có thể > gốc+lãi nếu có phạt)
                            .status(p.getStatus())
                            .paidDate(p.getPaidDate())
                            .build();
                })
                .collect(Collectors.toList());

        // 5. Cập nhật trạng thái hợp đồng (ACTIVE / OVERDUE / CLOSED)
        ContractStatus newStatus;
        if (payments.stream().allMatch(p -> p.getStatus() == PaymentStatus.PAID)) {
            newStatus = ContractStatus.CLOSED;
        } else if (payments.stream().anyMatch(p -> p.getStatus() == PaymentStatus.OVERDUE)) {
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
                .applicationCode(ct.getApplication().getCode())
                .customerName(ct.getApplication().getCustomerName())
                .customerPhone(ct.getApplication().getCustomerPhone())
                .productName(ct.getApplication().getProductName())
                .planName(ct.getPlan().getName())
                .totalLoan(ct.getTotalLoan())
                .remainingAmount(remainingAmount)        // dư nợ = tổng GỐC chưa trả
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

    // Dùng cho list để "Còn lại" khớp với chi tiết
    private long calculateRemainingAmount(Long contractId) {
        List<InstallmentPayment> payments =
                paymentRepo.findByContractIdOrderByPeriodNumberAsc(contractId);

        return payments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .mapToLong(InstallmentPayment::getPrincipalAmount)
                .sum();
    }

    /**
     * Tính tiền phạt cho 1 kỳ quá hạn:
     *  - LATE_PENALTY_RATE_PER_MONTH % mỗi tháng
     *  - tính trên toàn bộ amount của kỳ đó
     *  - tối thiểu 1 tháng nếu đã quá hạn.
     */
    private long calculatePenalty(InstallmentPayment payment, LocalDate today) {
        // nếu chưa tới hạn thì không phạt
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
        long remainingAmount = calculateRemainingAmount(ct.getId());

        return ContractResponse.builder()
                .id(ct.getId())
                .code(ct.getCode())
                .applicationCode(ct.getApplication().getCode())
                .customerName(ct.getApplication().getCustomerName())
                .productName(ct.getApplication().getProductName())
                .planName(ct.getPlan().getName())
                .totalLoan(ct.getTotalLoan())
                .remainingAmount(remainingAmount)
                .status(ct.getStatus())
                .build();
    }
}
