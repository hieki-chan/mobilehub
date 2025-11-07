package org.mobilehub.rating_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ratings",
        indexes = {
                @Index(name = "idx_ratings_product", columnList = "product_id"),
                @Index(name = "idx_ratings_created_at", columnList = "created_at")
        })
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "product_id", nullable = false)
    private Long productId;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(name = "username")
    private String username; // optional display name


    @Min(1) @Max(5)
    @Column(nullable = false)
    private int stars; // 1..5


    @Size(max = 2000)
    @Column(length = 2000)
    private String comment;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}