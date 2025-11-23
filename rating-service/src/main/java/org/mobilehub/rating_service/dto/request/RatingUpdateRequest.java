package org.mobilehub.rating_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RatingUpdateRequest(
        @NotNull Long ratingId,
        @Min(1) @Max(5) int stars,
        @Size(max = 2000) String comment
) {}