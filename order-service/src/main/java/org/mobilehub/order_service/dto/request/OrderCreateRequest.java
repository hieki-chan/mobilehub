package org.mobilehub.order_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mobilehub.order_service.entity.PaymentMethod;
import org.mobilehub.order_service.entity.ShippingMethod;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreateRequest {
    @NotNull
    Long userId;
    @NotNull
    PaymentMethod paymentMethod;
    @NotNull
    ShippingMethod shippingMethod;
    @NotNull
    String note;
    @NotNull
    List<OrderItemRequest> items;
}
