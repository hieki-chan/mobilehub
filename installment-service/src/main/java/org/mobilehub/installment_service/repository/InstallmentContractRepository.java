package org.mobilehub.installment_service.repository;

import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InstallmentContractRepository
        extends JpaRepository<InstallmentContract, Long> {

    long countByStatus(ContractStatus status);
    @Query("select coalesce(sum(c.totalLoan), 0) from InstallmentContract c")
    long sumTotalLoan();

    @Query("select coalesce(sum(c.remainingAmount),0) from InstallmentContract c where c.status = :status")
    long sumRemainingByStatus(@Param("status") ContractStatus status);
    boolean existsByApplicationId(Long appId);
    List<InstallmentContract> findByCreatedAtAfter(LocalDateTime createdAt);
}
