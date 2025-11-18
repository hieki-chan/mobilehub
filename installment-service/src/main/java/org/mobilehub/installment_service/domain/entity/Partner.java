package org.mobilehub.installment_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String contactPhone;
}
