package org.mobilehub.installment_service.dto.application;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {

    @NotNull
    private ApplicationStatus status;
}
