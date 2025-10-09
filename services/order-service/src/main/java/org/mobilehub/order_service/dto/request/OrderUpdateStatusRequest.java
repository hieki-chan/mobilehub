package org.mobilehub.order_service.dto.request;

import lombok.*;
import org.mobilehub.order_service.entity.OrderStatus;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderUpdateStatusRequest {
    private OrderStatus status;
}
