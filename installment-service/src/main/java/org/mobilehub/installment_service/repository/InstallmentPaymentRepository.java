package org.mobilehub.installment_service.repository;

import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InstallmentPaymentRepository
        extends JpaRepository<InstallmentPayment, Long> {

    // Lấy lịch thanh toán của 1 hợp đồng, sắp xếp theo kỳ
    List<InstallmentPayment> findByContractIdOrderByPeriodNumberAsc(Long contractId);

    // Đếm số kỳ theo trạng thái (dùng nếu cần)
    long countByContractIdAndStatus(Long contractId, PaymentStatus status);
    
    // Tìm các khoản thanh toán đến hạn trong khoảng thời gian và theo trạng thái
    List<InstallmentPayment> findByDueDateBetweenAndStatus(
            LocalDate startDate, 
            LocalDate endDate, 
            PaymentStatus status
    );
}
