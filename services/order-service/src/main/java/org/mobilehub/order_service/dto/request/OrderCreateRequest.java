package org.mobilehub.order_service.dto.request;

import lombok.*;
import org.mobilehub.order_service.entity.PaymentMethod;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderCreateRequest {
    private Long userId;
    private PaymentMethod paymentMethod;
    private String note;
    private List<OrderItemRequest> items;
}
