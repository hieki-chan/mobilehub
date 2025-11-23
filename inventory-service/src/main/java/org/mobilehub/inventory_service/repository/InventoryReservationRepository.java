package org.mobilehub.inventory_service.repository;

import org.mobilehub.inventory_service.entity.InventoryReservation;
import org.mobilehub.inventory_service.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    // Tìm reservation bằng reservationId (UUID dùng cho giao tiếp với các service khác)
    Optional<InventoryReservation> findByReservationId(String reservationId);

    // Tìm bằng idempotencyKey để chống xử lý trùng
    Optional<InventoryReservation> findByIdempotencyKey(String idempotencyKey);

    // Lấy tất cả reservation đã hết hạn mà vẫn ở trạng thái PENDING
    List<InventoryReservation> findAllByStatusAndExpiresAtBefore(ReservationStatus status, Instant expiresAt);

    @Query("""
    select r from InventoryReservation r
    left join fetch r.items
    where r.reservationId = :reservationId
""")
    Optional<InventoryReservation> findByReservationIdWithItems(@Param("reservationId") String reservationId);
}
