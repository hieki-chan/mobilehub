package org.mobilehub.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "idempotency_key", uniqueConstraints = {
    @UniqueConstraint(name = "ux_idem_key_endpoint", columnNames = {"keyValue", "endpoint"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IdempotencyKey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keyValue", nullable = false, length = 128)
    private String key;

    @Column(nullable = false, length = 128)
    private String endpoint;

    @Column(nullable = false, length = 128)
    private String requestHash;

    // link to domain entity (for create intent)
    private Long paymentId;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
