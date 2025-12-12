package org.mobilehub.installment_service.event;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InstallmentOrderCreateRequestedEvent {

    private String eventId;       // UUID
    private Instant occurredAt;

    private Long applicationId;   // installment application id
    private String applicationCode;

    private Long userId;

    // order item (1 sản phẩm)
    private Long productId;
    private Long variantId;
    private Integer quantity;

    // shipping
    private Long addressId;

    // metadata
    private Integer tenorMonths;
    private Long planId;

    private String note;          // ghi chú cho order
}