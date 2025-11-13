package org.mobilehub.customer_service.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Table
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    @Id
    Long id; // = userId

    @Nullable
    @Column(length = 12, unique = true)
    String identityNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Address> addresses;

    @OneToOne
    @JoinColumn(name = "default_address_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Address defaultAddress;
}
