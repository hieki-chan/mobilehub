package org.mobilehub.order_service.dto.response;

import lombok.Data;

@Data
public class MonthlyOrderCountResponse {
    private int year;
    private int month;
    private long orderCount;

    public MonthlyOrderCountResponse(int year, int month, long orderCount) {
        this.year = year;
        this.month = month;
        this.orderCount = orderCount;
    }
}
