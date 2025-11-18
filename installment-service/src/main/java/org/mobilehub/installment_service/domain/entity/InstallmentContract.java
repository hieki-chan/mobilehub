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
}
