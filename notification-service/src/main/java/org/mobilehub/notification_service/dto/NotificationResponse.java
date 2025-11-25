package org.mobilehub.notification_service.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record NotificationResponse(
        Long id,
        String title,
        String body,
        String type,
        String status,
        String refType,
        String refId,
        Instant createdAt,
        Instant readAt
) {}
