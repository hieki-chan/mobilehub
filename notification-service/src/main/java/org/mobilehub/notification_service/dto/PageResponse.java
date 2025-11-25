package org.mobilehub.notification_service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages
) {}
