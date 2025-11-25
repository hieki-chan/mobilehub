package org.mobilehub.notification_service.repository;

import org.mobilehub.notification_service.entity.Notification;
import org.mobilehub.notification_service.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, NotificationStatus status, Pageable pageable);
    Optional<Notification> findByIdAndUserId(Long id, String userId);
    long countByUserIdAndStatus(String userId, NotificationStatus status);
}
