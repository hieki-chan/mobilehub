package org.mobilehub.installment_service.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationPrecheckResponse {

    private boolean eligible;   // true = đủ điều kiện
    private String message;     // lý do nếu không đủ, hoặc thông báo OK
}
