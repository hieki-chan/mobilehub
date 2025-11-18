package org.mobilehub.installment_service.service.impl;

import org.mobilehub.installment_service.domain.entity.Partner;
import org.mobilehub.installment_service.dto.plan.PlanCreateRequest;
import org.mobilehub.installment_service.dto.plan.PlanResponse;
import org.mobilehub.installment_service.repository.InstallmentPlanRepository;
import org.mobilehub.installment_service.repository.PartnerRepository;
import org.mobilehub.installment_service.service.InstallmentPlanService;
import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentPlanServiceImpl implements InstallmentPlanService {

    private final InstallmentPlanRepository planRepo;
    private final PartnerRepository partnerRepo;

    @Override
    public List<PlanResponse> getAllPlans() {
        return planRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlanResponse createPlan(PlanCreateRequest request) {
        Partner partner = partnerRepo.findById(request.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        InstallmentPlan plan = InstallmentPlan.builder()
                .code(request.getCode())
                .name(request.getName())
                .partner(partner)
                .minPrice(request.getMinPrice())
                .downPaymentPercent(request.getDownPaymentPercent())
                .interestRate(request.getInterestRate())
                .allowedTenors(request.getAllowedTenors())
                .active(request.isActive())
                .build();

        planRepo.save(plan);
        return toResponse(plan);
    }

    private PlanResponse toResponse(InstallmentPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())
                .name(plan.getName())
                .partnerName(plan.getPartner().getName())
                .minPrice(plan.getMinPrice())
                .downPaymentPercent(plan.getDownPaymentPercent())
                .interestRate(plan.getInterestRate())
                .allowedTenors(plan.getAllowedTenors())
                .active(plan.isActive())
                .build();
    }
}
