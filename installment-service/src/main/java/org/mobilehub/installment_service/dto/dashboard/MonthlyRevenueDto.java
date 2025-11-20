package org.mobilehub.installment_service.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyRevenueDto {
    private String monthLabel; // ví dụ: "T1", "T2", "T3"...
    private long revenue;      // tổng totalLoan trong tháng đó
}
