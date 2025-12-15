package org.mobilehub.installment_service.controller;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.mobilehub.installment_service.dto.application.ApplicationFilter;
import org.mobilehub.installment_service.dto.application.ApplicationResponse;
import org.mobilehub.installment_service.dto.application.ApplicationStatusUpdateRequest;
import org.mobilehub.installment_service.service.InstallmentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
            @Valid @RequestBody ApplicationCreateRequest request,
            Authentication authentication
    ) {
        // ✅ Lấy userId từ JWT token thay vì từ request body
        Long userId = extractUserIdFromToken(authentication);
        request.setUserId(userId);
        return applicationService.create(request);
    }
    
    private Long extractUserIdFromToken(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // JWT token có claim "id" (từ identity-service)
            Object idClaim = jwt.getClaim("id");
            if (idClaim != null) {
                if (idClaim instanceof Number) {
                    return ((Number) idClaim).longValue();
                }
                return Long.parseLong(idClaim.toString());
            }
            
            // Fallback: thử lấy từ subject
            String sub = jwt.getSubject();
            if (sub != null) {
                try {
                    return Long.parseLong(sub);
                } catch (NumberFormatException e) {
                    // subject không phải là userId
                }
            }
            
            throw new IllegalStateException("Cannot extract userId from token. Available claims: " + jwt.getClaims().keySet());
        }
        throw new IllegalStateException("Cannot extract userId from token - authentication is null or not JWT");
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
