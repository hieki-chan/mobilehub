package org.mobilehub.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mobilehub.notification_service.enums.NotificationStatus;
import org.mobilehub.notification_service.enums.NotificationType;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user", columnList = "userId,createdAt"),
                @Index(name = "idx_notifications_status", columnList = "userId,status")
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // subject from JWT

    private String title;

    @Column(length = 2000)
    private String body;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String refType;
    private String refId;

    private Instant createdAt;
    private Instant readAt;

    public void markRead() {
        this.status = NotificationStatus.READ;
        this.readAt = Instant.now();
    }
}
