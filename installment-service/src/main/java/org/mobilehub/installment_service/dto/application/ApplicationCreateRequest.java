package org.mobilehub.installment_service.dto.application;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ApplicationCreateRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    @NotBlank
    private String productName;

    @NotNull @Positive
    private Long productPrice;

    @NotNull @Positive
    private Long loanAmount;

    @NotNull
    private Long planId;

    // NEW: tenor chosen by customer/admin (must be one of plan.allowedTenors)
    @NotNull
    @Positive
    private Integer tenorMonths;

    // ✅ Không cần @NotNull vì sẽ lấy từ JWT token
    private Long userId;        // id user trong hệ thống TMĐT (sẽ được set từ token)

    @NotNull
    private Long productId;     // id sản phẩm trong product-service

    @NotNull
    private Long variantId;     // id biến thể (màu, dung lượng...)

    @NotNull
    @Positive
    private Integer quantity;   // số lượng mua

    @NotNull
    private Long addressId;     // id địa chỉ giao hàng mặc định

}
