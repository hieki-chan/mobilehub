package org.mobilehub.order_service.repository;

import org.mobilehub.order_service.entity.InstallmentOrderMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallmentOrderMappingRepository
        extends JpaRepository<InstallmentOrderMapping, Long> {

    boolean existsByApplicationId(Long applicationId);
}
