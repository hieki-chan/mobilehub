package org.mobilehub.installment_service.repository;

import org.mobilehub.installment_service.domain.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
