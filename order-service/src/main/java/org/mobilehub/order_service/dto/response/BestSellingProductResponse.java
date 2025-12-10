package org.mobilehub.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestSellingProductResponse {
    private Long productId;
    private String productName;
    private Long totalSold; // tổng số lượng bán
}
