package org.mobilehub.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "username", nullable = false,  unique = true)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @Column
    @Enumerated(EnumType.STRING)
    SignInProvider provider = SignInProvider.EMAIL_AND_PASSWORD;

    @Column
    @Enumerated(EnumType.STRING)
    Role role;

    @Column
    private LocalDateTime createdAt;
    //private LocalDateTime updatedAt;

    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        //updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        //updatedAt = LocalDateTime.now();
    }
}
