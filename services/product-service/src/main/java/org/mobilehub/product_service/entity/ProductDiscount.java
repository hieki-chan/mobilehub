package org.mobilehub.product_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_discount")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    Double value_in_percent;

    @Column
    LocalDateTime startDate;

    @Column
    LocalDateTime endDate;

    @OneToOne(mappedBy = "discount")
    private Product product;

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (startDate.isBefore(now) || startDate.isEqual(now))
                && (endDate.isAfter(now) || endDate.isEqual(now));
    }
}
