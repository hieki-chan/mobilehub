package org.mobilehub.customer_service.repository;

import org.mobilehub.customer_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndCustomerId(Long id, Long userId);
    List<Address> findAllByCustomerId(Long userId);
}
