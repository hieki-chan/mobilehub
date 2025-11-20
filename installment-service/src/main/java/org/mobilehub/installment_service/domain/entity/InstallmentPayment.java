package org.mobilehub.installment_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;

import java.time.LocalDate;

@Entity
@Table(name = "installment_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hợp đồng nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private InstallmentContract contract;

    // Kỳ số: 1,2,3...
    @Column(nullable = false)
    private Integer periodNumber;

    // Ngày đến hạn
    @Column(nullable = false)
    private LocalDate dueDate;

    // Tổng số tiền phải trả kỳ này
    @Column(nullable = false)
    private Long amount;

    // Optional: tách gốc / lãi (nếu k cần thì vẫn để được)
    @Column
    private Long principalAmount;

    @Column
    private Long interestAmount;

    // Trạng thái thanh toán
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    // Ngày thực tế đã trả (nếu đã thanh toán)
    private LocalDate paidDate;
}
