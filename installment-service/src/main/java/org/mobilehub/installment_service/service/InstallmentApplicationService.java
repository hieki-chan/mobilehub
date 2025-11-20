package org.mobilehub.installment_service.service;

import org.mobilehub.installment_service.dto.application.*;

import java.util.List;

public interface InstallmentApplicationService {

    // Bước 1: Precheck
    ApplicationPrecheckResponse precheck(ApplicationPrecheckRequest request);

    // Bước 2: Tạo hồ sơ PENDING sau khi precheck OK
    ApplicationResponse create(ApplicationCreateRequest request);

    // Bước 4: Admin xem list hồ sơ (đã có)
    List<ApplicationResponse> searchApplications(ApplicationFilter filter);

    // Admin duyệt / từ chối (đã có)
    void updateStatus(Long id, ApplicationStatusUpdateRequest request);
}
