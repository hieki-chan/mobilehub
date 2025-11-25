package org.mobilehub.notification_service.port;

import org.mobilehub.notification_service.entity.Notification;
import org.mobilehub.notification_service.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NotificationRepositoryPort {
    Notification save(Notification n);
    Page<Notification> findByUserId(String userId, Pageable pageable);
    Page<Notification> findByUserIdAndStatus(String userId, NotificationStatus status, Pageable pageable);
    Optional<Notification> findByIdAndUserId(Long id, String userId);
    long countUnread(String userId);
}
