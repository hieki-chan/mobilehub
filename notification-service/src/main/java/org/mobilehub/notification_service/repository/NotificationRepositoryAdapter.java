package org.mobilehub.notification_service.repository;

import lombok.RequiredArgsConstructor;
import org.mobilehub.notification_service.entity.Notification;
import org.mobilehub.notification_service.enums.NotificationStatus;
import org.mobilehub.notification_service.port.NotificationRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository repo;

    @Override
    public Notification save(Notification n) {
        return repo.save(n);
    }

    @Override
    public Page<Notification> findByUserId(String userId, Pageable pageable) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public Page<Notification> findByUserIdAndStatus(String userId, NotificationStatus status, Pageable pageable) {
        return repo.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
    }

    @Override
    public Optional<Notification> findByIdAndUserId(Long id, String userId) {
        return repo.findByIdAndUserId(id, userId);
    }

    @Override
    public long countUnread(String userId) {
        return repo.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }
}
