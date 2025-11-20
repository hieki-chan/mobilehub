//package org.mobilehub.customer_service.entity;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Data;
//import lombok.experimental.FieldDefaults;
//
//import java.time.LocalDate;

//@Table
//@Data
//@Entity
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class UserProfile {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @Column(nullable = false)
//    String fullName;
//
//    @Column(nullable = false, unique = true)
//    String email;
//
//    @Column(length = 15)
//    String phoneNumber;
//
//    @Enumerated(EnumType.STRING)
//    Gender gender;
//
//    LocalDate birthDate;
//
//    @OneToOne(mappedBy = "userProfile")
//    Customer customer;
//
//    public enum Gender {
//        MALE, FEMALE, OTHER
//    }
//}

