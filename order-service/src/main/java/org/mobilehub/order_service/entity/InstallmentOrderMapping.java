package org.mobilehub.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "installment_order_mapping",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_installment_application", columnNames = "application_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstallmentOrderMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "application_id", nullable = false)
    Long applicationId;

    @Column(name = "order_id", nullable = false)
    Long orderId;
}
