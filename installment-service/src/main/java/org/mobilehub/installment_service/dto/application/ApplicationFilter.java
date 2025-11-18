package org.mobilehub.installment_service.dto.application;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationFilter {
    private ApplicationStatus status;
    private Long partnerId;
    private String q;
}
