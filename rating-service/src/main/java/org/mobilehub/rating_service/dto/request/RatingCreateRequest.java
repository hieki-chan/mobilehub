package org.mobilehub.rating_service.dto.request;

import jakarta.validation.constraints.*;

public record RatingCreateRequest(
        @NotNull Long productId,
        @NotNull Long userId,
        @Min(1) @Max(5) int stars,
        @Size(max = 2000) String comment
) {}