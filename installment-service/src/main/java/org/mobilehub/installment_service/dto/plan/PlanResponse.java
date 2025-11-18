package org.mobilehub.installment_service.dto.plan;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanResponse {
    private Long id;
    private String code;
    private String name;
    private String partnerName;
    private Long minPrice;
    private Integer downPaymentPercent;
    private Double interestRate;
    private String allowedTenors;
    private boolean active;
}
