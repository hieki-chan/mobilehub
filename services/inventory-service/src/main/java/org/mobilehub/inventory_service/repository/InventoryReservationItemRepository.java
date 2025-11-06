package org.mobilehub.inventory_service.repository;

import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface InventoryReservationItemRepository extends JpaRepository<InventoryReservationItem, Long> {
    // Lấy toàn bộ item theo reservation cha
    List<InventoryReservationItem> findByReservationId(Long reservationId);

}
