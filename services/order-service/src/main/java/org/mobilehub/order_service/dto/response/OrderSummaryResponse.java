package org.mobilehub.order_service.dto.response;

import lombok.*;
import org.mobilehub.order_service.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {
    private Long id;
    private BigDecimal totalAmount;
    private String status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}