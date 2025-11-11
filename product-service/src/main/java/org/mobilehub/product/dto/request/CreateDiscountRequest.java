package org.mobilehub.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateDiscountRequest {
//    @NotNull(message = "Product ID is required")
//    private Long productId;

    @NotNull
    @Min(value = 5, message = "Percent must be greater than 0")
    @Max(value = 100, message = "Percent must be less than or equal to 100")
    private Integer valueInPercent;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}
