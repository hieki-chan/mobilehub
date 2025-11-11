package org.mobilehub.inventory_service.repository;

import jakarta.persistence.LockModeType;
import org.mobilehub.inventory_service.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
    //tim ton kho theo productId
    Optional<InventoryStock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InventoryStock s WHERE s.productId = :pid")
    Optional<InventoryStock> lockByProductId(@Param("pid") Long productId);
}
