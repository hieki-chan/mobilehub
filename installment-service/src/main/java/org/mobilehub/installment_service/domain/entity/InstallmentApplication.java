package org.mobilehub.installment_service.domain.entity;

import org.mobilehub.installment_service.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "installment_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String customerName;

    @Column(length = 20)
    private String customerPhone;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Long loanAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private InstallmentPlan plan;

    @Column(nullable = false)
    private Integer tenorMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ===================== [NEW] – dữ liệu TMĐT để tạo Order =====================

    /** Id user trong hệ thống thương mại điện tử (user đã tạo hồ sơ trả góp) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Id sản phẩm bên product-service */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** Id biến thể sản phẩm (màu, dung lượng…) */
    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    /** Số lượng sản phẩm muốn mua */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** Id địa chỉ giao hàng mặc định của user */
    @Column(name = "address_id", nullable = false)
    private Long addressId;
}
