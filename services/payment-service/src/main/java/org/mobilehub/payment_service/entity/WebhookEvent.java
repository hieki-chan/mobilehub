package org.mobilehub.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "payment_webhook_event", indexes = {
    @Index(name = "ux_webhook_event_event_id", columnList = "eventId", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WebhookEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String eventId;

    @Column(nullable = false, length = 32)
    private String provider;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "payment_id")
    private Payment payment;

    private Long orderCode;

    @Column(nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 128)
    private String payloadHash;

    @PrePersist
    public void prePersist() {
        if (occurredAt == null) occurredAt = Instant.now();
    }
}
