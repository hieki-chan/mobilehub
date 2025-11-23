package org.mobilehub.customer_service.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Table
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    @Id
    Long id; // = userId

    // CCCD INFO
    @Nullable
    @Column(length = 12, unique = true)
    String identityNumber;

    @Nullable
    @Column(length = 50)
    String fullName;

    @Nullable
    @Column(length = 12)
    LocalDate dateOfBirth;

    @Nullable
    @Column(length = 10)
    String sex;

    @Nullable
    @Column(length = 100)
    String placeOfResidence;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Address> addresses;

    @OneToOne
    @JoinColumn(name = "default_address_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Address defaultAddress;

    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    //private UserProfile userProfile;

    public Boolean getStatus() {
        return identityNumber != null;
    }

    public String getCccdNo() {
        return identityNumber;
    }
}
