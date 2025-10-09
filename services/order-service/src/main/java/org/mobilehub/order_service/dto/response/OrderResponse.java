package org.mobilehub.order_service.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String shippingAddress;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemResponse> items;
}
