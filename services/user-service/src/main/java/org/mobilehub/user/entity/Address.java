package org.mobilehub.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private Long userId;

    @NotBlank
    @Size(max = 500)
    @Column()
    private String street;

    @NotBlank
    @Size(max = 150)
    @Column()
    private String city;

    @Column(nullable = false)
    private boolean isDefault = false;
}
