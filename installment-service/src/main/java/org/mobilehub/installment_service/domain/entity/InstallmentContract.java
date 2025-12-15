package org.mobilehub.installment_service.domain.entity;

import org.mobilehub.installment_service.domain.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "installment_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private InstallmentApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private InstallmentPlan plan;

    @Column(nullable = false)
    private Long totalLoan;

    @Column(nullable = false)
    private Long remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // ============ PAYMENT INFO ============
    @Column(name = "down_payment_amount")
    private Long downPaymentAmount;  // Số tiền trả trước
    
    @Column(name = "payment_code")
    private Long paymentCode;  // Mã thanh toán từ payment-service
    
    @Column(name = "payment_url", length = 500)
    private String paymentUrl;  // URL thanh toán QR
    
    @Column(name = "down_payment_status", length = 20)
    private String downPaymentStatus;  // PENDING, PAID, FAILED
}
