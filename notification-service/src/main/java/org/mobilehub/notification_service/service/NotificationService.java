package org.mobilehub.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.notification_service.dto.NotificationResponse;
import org.mobilehub.notification_service.dto.PageResponse;
import org.mobilehub.notification_service.entity.Notification;
import org.mobilehub.notification_service.enums.NotificationStatus;
import org.mobilehub.notification_service.enums.NotificationType;
import org.mobilehub.notification_service.port.NotificationRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryPort repo;

    @Transactional
    public Notification create(String userId, String title, String body,
                               NotificationType type, String refType, String refId) {

        Notification n = Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .refType(refType)
                .refId(refId)
                .createdAt(Instant.now())
                .build();

        return repo.save(n);
    }

    public PageResponse<NotificationResponse> getUserNotifications(String userId, int page, int size, String status) {
        var pageable = PageRequest.of(page, size);

        var p = (status == null)
                ? repo.findByUserId(userId, pageable)
                : repo.findByUserIdAndStatus(userId, NotificationStatus.valueOf(status), pageable);

        var items = p.map(this::toResponse).toList();

        return PageResponse.<NotificationResponse>builder()
                .items(items)
                .page(page)
                .size(size)
                .totalItems(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    @Transactional
    public NotificationResponse markRead(String userId, Long id) {
        var n = repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (n.getStatus() == NotificationStatus.UNREAD) {
            n.markRead();
            repo.save(n);
        }

        return toResponse(n);
    }

    public long countUnread(String userId) {
        return repo.countUnread(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .type(n.getType().name())
                .status(n.getStatus().name())
                .refType(n.getRefType())
                .refId(n.getRefId())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }
}
