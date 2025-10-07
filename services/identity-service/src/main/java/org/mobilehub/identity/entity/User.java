package org.mobilehub.identity.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "phone-number", unique = true)
    String phoneNumber;

    @Column(name = "username", nullable = false,  unique = true)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    private LocalDateTime createdAt;
    //private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        //updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        //updatedAt = LocalDateTime.now();
    }

    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roleSet;
}
