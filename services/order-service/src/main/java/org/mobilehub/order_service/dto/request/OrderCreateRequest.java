package org.mobilehub.order_service.dto.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderCreateRequest {
    private Long userId;
    private String paymentMethod;
    private String note;
    private List<OrderItemRequest> items;
}
