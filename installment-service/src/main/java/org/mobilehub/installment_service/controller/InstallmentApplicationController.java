package org.mobilehub.installment_service.controller;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.dto.application.ApplicationFilter;
import org.mobilehub.installment_service.dto.application.ApplicationResponse;
import org.mobilehub.installment_service.dto.application.ApplicationStatusUpdateRequest;
import org.mobilehub.installment_service.service.InstallmentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class InstallmentApplicationController {

    private final InstallmentApplicationService applicationService;

    @GetMapping
    public List<ApplicationResponse> search(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) Long partnerId,
            @RequestParam(required = false) String q
    ) {
        ApplicationFilter filter = new ApplicationFilter();
        filter.setStatus(status);
        filter.setPartnerId(partnerId);
        filter.setQ(q);
        return applicationService.searchApplications(filter);
    }

    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id,
                             @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        applicationService.updateStatus(id, request);
    }
}
