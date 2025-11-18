package org.mobilehub.installment_service.service;

import org.mobilehub.installment_service.dto.application.ApplicationFilter;
import org.mobilehub.installment_service.dto.application.ApplicationResponse;
import org.mobilehub.installment_service.dto.application.ApplicationStatusUpdateRequest;

import java.util.List;

public interface InstallmentApplicationService {

    List<ApplicationResponse> searchApplications(ApplicationFilter filter);

    void updateStatus(Long id, ApplicationStatusUpdateRequest request);
}
