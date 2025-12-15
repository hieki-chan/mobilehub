package org.mobilehub.order_service.repository;

import org.mobilehub.order_service.entity.InstallmentOrderMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstallmentOrderMappingRepository
        extends JpaRepository<InstallmentOrderMapping, Long> {

    boolean existsByApplicationId(Long applicationId);
    
    Optional<InstallmentOrderMapping> findByApplicationId(Long applicationId);
}
