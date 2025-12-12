package org.mobilehub.installment_service.messaging;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstallmentOrderCreateMessage {

    Long applicationId;   // id hồ sơ trả góp
    Long userId;          // user tạo hồ sơ

    String paymentMethod;   // ví dụ: "INSTALLMENT"
    String shippingMethod;  // ví dụ: "DELIVERY"
    String note;            // ghi chú cho order
    Long addressId;         // id địa chỉ giao hàng

    List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Item {
        Long productId;
        Long variantId;
        Integer quantity;
    }
}
