package org.mobilehub.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mobilehub.order_service.entity.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusCountResponse {

    private OrderStatus status;
    private Long count;
}
