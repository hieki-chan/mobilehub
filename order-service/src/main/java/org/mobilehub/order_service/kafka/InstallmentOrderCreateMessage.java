package org.mobilehub.order_service.kafka;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentOrderCreateMessage {

    private Long applicationId;  // id hồ sơ trả góp
    private Long userId;         // id user trong hệ thống TMĐT

    private String paymentMethod;   // "INSTALLMENT"
    private String shippingMethod;  // "DELIVERY" (hoặc tên enum bên bạn)
    private String note;
    private Long addressId;

    List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long productId;
        private Long variantId;
        private Integer quantity;
    }
}
