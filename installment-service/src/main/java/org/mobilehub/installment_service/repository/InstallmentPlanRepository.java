package org.mobilehub.installment_service.repository;

import org.mobilehub.installment_service.domain.entity.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, Long> {
}
