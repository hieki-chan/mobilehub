package org.mobilehub.installment_service.controller;

import org.mobilehub.installment_service.dto.plan.PlanCreateRequest;
import org.mobilehub.installment_service.dto.plan.PlanResponse;
import org.mobilehub.installment_service.service.InstallmentPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class InstallmentPlanController {

    private final InstallmentPlanService planService;

    @GetMapping
    public List<PlanResponse> getAllPlans() {
        return planService.getAllPlans();
    }

    @PostMapping
    public PlanResponse createPlan(@Valid @RequestBody PlanCreateRequest request) {
        return planService.createPlan(request);
    }

    @DeleteMapping("/{id}")
    public PlanResponse deactivatePlan(@PathVariable Long id) {
        return planService.deactivatePlan(id);
    }
}
