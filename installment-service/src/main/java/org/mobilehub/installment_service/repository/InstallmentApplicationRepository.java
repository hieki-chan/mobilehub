package org.mobilehub.installment_service.repository;

import org.mobilehub.installment_service.domain.entity.InstallmentApplication;
import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstallmentApplicationRepository
        extends JpaRepository<InstallmentApplication, Long> {

    long countByStatus(ApplicationStatus status);
    List<InstallmentApplication> findTop5ByStatusOrderByCreatedAtDesc(ApplicationStatus status);
}
