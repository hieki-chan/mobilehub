package org.mobilehub.installment_service.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardOverviewDto {

    // ==== Ô số liệu lớn ====
    private long totalRevenue;        // Doanh số giải ngân (tổng totalLoan)
    private long outstandingDebt;     // Tổng dư nợ hiện tại (tổng gốc chưa trả)
    private long overdueContracts;    // Số HĐ quá hạn
    private long activeContracts;     // Số HĐ đang hiệu lực

    // ==== Trạng thái hồ sơ ====
    private long totalApplications;    // Tổng hồ sơ
    private long approvedApplications; // Đã duyệt
    private long pendingApplications;  // Đang chờ duyệt
    private long rejectedApplications; // Từ chối

    // ==== Tăng trưởng doanh số so với tháng trước (ví dụ +12%) ====
    private double revenueGrowthPercent; // % tăng/giảm doanh số tháng này vs tháng trước

    // ==== Biểu đồ doanh thu 6 tháng ====
    private List<MonthlyRevenueDto> revenueChart;

    // ==== Hồ sơ chờ duyệt gần nhất (bảng bên phải) ====
    private List<RecentApplicationDto> recentPendingApplications;
}
