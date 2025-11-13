package org.mobilehub.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_user_created", columnList = "user_id,created_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 32)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;

    @Column()
    private String note;

    // ===== fields phục vụ tích hợp inventory =====
    @Column(name = "reservation_id", length = 64)
    private String reservationId;

    @Column(name = "reserved_until")
    private Instant reservedUntil;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    // ===== timestamps =====
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ===== items =====
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
