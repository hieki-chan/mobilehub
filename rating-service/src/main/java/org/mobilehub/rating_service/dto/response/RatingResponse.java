package org.mobilehub.rating_service.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record RatingResponse(
        Long id,
        Long productId,
        Long userId,
        String username,
        int stars,
        String comment,
        Instant createdAt,
        Instant updatedAt,
        ReplyDto reply
) {
    public record ReplyDto(Long id, String content, String adminName, Long adminId, Instant createdAt, Instant updatedAt) {}
}