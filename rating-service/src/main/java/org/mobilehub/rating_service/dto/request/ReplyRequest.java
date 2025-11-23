package org.mobilehub.rating_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ReplyRequest(
        @NotBlank @Size(max = 2000) String content,
        Long adminId
) {}