package org.mobilehub.installment_service.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecentApplicationDto {
    private String customerName;
    private String productName;
    private String planName;
    private LocalDateTime createdAt; // FE tự format "10:30" / "dd/MM"
}
