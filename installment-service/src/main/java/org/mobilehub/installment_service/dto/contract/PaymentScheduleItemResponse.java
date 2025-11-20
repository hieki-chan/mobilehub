package org.mobilehub.installment_service.dto.contract;

import lombok.Builder;
import lombok.Data;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;

import java.time.LocalDate;

@Data
@Builder
public class PaymentScheduleItemResponse {
    private Integer period;       // Kỳ số
    private LocalDate dueDate;    // Ngày đến hạn
    private Long principalAmount;
    private Long interestAmount;
    private Long amount;          // Số tiền phải trả
    private PaymentStatus status; // PLANNED / PAID / OVERDUE
    private LocalDate paidDate;   // Ngày đã trả (nếu có)
}
