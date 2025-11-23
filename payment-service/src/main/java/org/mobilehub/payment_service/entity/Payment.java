package org.mobilehub.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment", indexes = {
        @Index(name = "ux_payment_order_code", columnList = "orderCode", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false, unique = true)
    private Long orderCode;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 8)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CaptureMethod captureMethod;

    @Column(nullable = false, length = 32)
    private String provider; // PAYOS, VNPAY, etc

    private String providerIntentId;
    private String providerPaymentId;
    private String clientSecret;

    @Column(length = 1024)
    private String paymentUrl;

    private String failureCode;
    private String failureMessage;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal capturedAmount;

    @Version
    private Long version;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
        if (capturedAmount == null) capturedAmount = BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void markAuthorized() {
        this.status = PaymentStatus.AUTHORIZED;
    }

    public void capture(BigDecimal amount) {
        if (this.capturedAmount == null) this.capturedAmount = BigDecimal.ZERO;
        this.capturedAmount = this.capturedAmount.add(amount);
        if (this.capturedAmount.compareTo(this.amount) >= 0) {
            this.status = PaymentStatus.CAPTURED;
        } else {
            this.status = PaymentStatus.PARTIALLY_CAPTURED;
        }
    }

    public void fail(String code, String message) {
        this.status = PaymentStatus.FAILED;
        this.failureCode = code;
        this.failureMessage = message;
    }
}
