package org.mobilehub.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class MonthlySalesResponse {
    private int year;
    private int month;
    private BigDecimal totalAmount;
}
