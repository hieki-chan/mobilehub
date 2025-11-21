package org.mobilehub.installment_service.dto.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.mobilehub.installment_service.domain.enums.ContractStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ContractDetailResponse {

    // Phần summary & thông tin hợp đồng
    private Long id;
    private String code;              // CT-001

    private String applicationCode;   // APP-002
    private String customerName;
    private String customerPhone;
    private String productName;
    private String planName;

    private Long totalLoan;           // Tổng vay
    private Long remainingAmount;     // Còn lại
    private ContractStatus status;    // Đang hiệu lực / Đã tất toán / Quá hạn

    private LocalDate startDate;
    private LocalDate endDate;

    // Tiến độ
    private Integer totalPeriods;     // Tổng số kỳ
    private Integer paidPeriods;      // Kỳ đã trả

    // Lịch thanh toán chi tiết
    @JsonProperty("paymentSchedule")
    private List<PaymentScheduleItemResponse> schedule;
}
