package org.mobilehub.inventory_service.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "inventory_reservation_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private InventoryReservation reservation;
}
