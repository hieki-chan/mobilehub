package org.mobilehub.installment_service.service;

import org.mobilehub.installment_service.dto.plan.PlanCreateRequest;
import org.mobilehub.installment_service.dto.plan.PlanResponse;

import java.util.List;

public interface InstallmentPlanService {
    List<PlanResponse> getAllPlans();
    PlanResponse createPlan(PlanCreateRequest request);
    PlanResponse deactivatePlan(Long id);
}
