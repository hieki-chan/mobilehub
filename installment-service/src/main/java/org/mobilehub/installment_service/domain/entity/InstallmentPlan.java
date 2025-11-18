package org.mobilehub.installment_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "installment_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(nullable = false)
    private Long minPrice;

    @Column(nullable = false)
    private Integer downPaymentPercent;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false, length = 50)
    private String allowedTenors;

    @Column(nullable = false)
    private boolean active;
}
