package org.mobilehub.order_service.dto.response;

import lombok.*;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.entity.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemResponse> items;
}
