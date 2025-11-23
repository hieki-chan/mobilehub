package org.mobilehub.inventory_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Ma dinh danh duy nhat cho lan giu hang
    @Column(nullable = false, unique = true)
    private String reservationId;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    //Ma dinh danh ngan chan viec xu ly trung su kien
    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant expiresAt;

    @OneToMany(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<InventoryReservationItem> items = new ArrayList<>();

}
