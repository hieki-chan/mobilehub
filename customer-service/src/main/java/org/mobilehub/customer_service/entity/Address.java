package org.mobilehub.customer_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @Column(length = 100)
    String fullName;

    @Column(length = 15)
    String phoneNumber;

    // address
    @Size(max = 50)
    @Column(length = 50)
    String province;

    @Size(max = 50)
    @Column(length = 50)
    String district;

    @Size(max = 50)
    @Column(length = 50)
    String ward;

    @Size(max = 200)
    @Column(length = 200)
    String addressDetail;

    // ggmap
    @Column(precision = 10, scale = 6)
    BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    BigDecimal longitude;
}

