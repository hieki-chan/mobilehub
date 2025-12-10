package org.mobilehub.product_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BrandDistributionResponse {
    private String brand;
    private Long totalSold;
    private BigDecimal totalRevenue;
}
