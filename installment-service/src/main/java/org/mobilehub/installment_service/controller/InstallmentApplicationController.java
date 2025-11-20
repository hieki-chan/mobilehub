package org.mobilehub.installment_service.controller;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.dto.application.ApplicationFilter;
import org.mobilehub.installment_service.dto.application.ApplicationResponse;
import org.mobilehub.installment_service.dto.application.ApplicationStatusUpdateRequest;
import org.mobilehub.installment_service.service.InstallmentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.mobilehub.installment_service.dto.application.ApplicationCreateRequest;
import org.mobilehub.installment_service.dto.application.ApplicationPrecheckRequest;
import org.mobilehub.installment_service.dto.application.ApplicationPrecheckResponse;


import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class InstallmentApplicationController {

    private final InstallmentApplicationService applicationService;

    // 1) Precheck cho site khách
    @PostMapping("/precheck")
    public ApplicationPrecheckResponse precheck(
            @Valid @RequestBody ApplicationPrecheckRequest request
    ) {
        return applicationService.precheck(request);
    }

    // 2) Tạo hồ sơ PENDING sau khi precheck OK
    @PostMapping
    public ApplicationResponse create(
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        return applicationService.create(request);
    }

    // 3) Search list hồ sơ
    @GetMapping
    public List<ApplicationResponse> search(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) Long partnerId,
            @RequestParam(required = false, name = "q") String q
    ) {
        ApplicationFilter filter = new ApplicationFilter();
        filter.setStatus(status);
        filter.setPartnerId(partnerId);
        filter.setQ(q);
        return applicationService.searchApplications(filter);
    }

    // 4) Admin cập nhật trạng thái
    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id,
                             @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        applicationService.updateStatus(id, request);
    }
}
