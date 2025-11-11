package org.mobilehub.payment_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_order_code", columnList = "orderCode", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderCode;

    /** Thông tin hiển thị đơn */
    private String productName;

    private String description;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Integer quantity;

    /** URL checkout do PayOS trả về để redirect */
    @Column(length = 1024)
    private String checkoutUrl;

    /** PENDING | PAID | CANCELLED | FAILED */
    @Column(length = 32, nullable = false)
    private String status;

    /** Lưu lại URLs để tham chiếu */
    @Column(length = 512) private String returnUrl;
    @Column(length = 512) private String cancelUrl;

    /** Một số hệ thống muốn lưu transactionId/trace id… (nếu có trong webhook) */
    @Column(length = 128) private String transactionId;

    /** Lưu raw webhook lần gần nhất để tiện debug/audit */
    @Lob @Column(columnDefinition = "LONGTEXT")
    private String lastWebhookRaw;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}