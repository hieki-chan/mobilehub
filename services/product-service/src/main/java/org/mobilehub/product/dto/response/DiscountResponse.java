package org.mobilehub.product.dto.response;

import lombok.Data;

@Data
public class DiscountResponse {
    private Integer valueInPercent;
    private String startDate;
    private String endDate;
}
