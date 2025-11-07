package org.mobilehub.rating_service.dto.response;

import java.util.List;

public record PageResponse<T>(List<T> items, int page, int size, long totalElements, int totalPages) {}