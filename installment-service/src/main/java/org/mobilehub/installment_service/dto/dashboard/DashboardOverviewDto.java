package org.mobilehub.installment_service.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardOverviewDto {
    private long totalApplications;
    private long pendingApplications;
    private long approvedApplications;
    private long activeContracts;
    private long totalOutstanding;
}
