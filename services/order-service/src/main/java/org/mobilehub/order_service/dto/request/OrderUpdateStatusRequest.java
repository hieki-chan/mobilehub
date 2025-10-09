package org.mobilehub.order_service.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderUpdateStatusRequest {
    private String status;
}
