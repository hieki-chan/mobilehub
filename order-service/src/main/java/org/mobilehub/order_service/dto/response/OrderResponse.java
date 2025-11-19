package org.mobilehub.order_service.dto.response;

import lombok.*;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Instant createdAt;
    private List<OrderItemResponse> items;
}
