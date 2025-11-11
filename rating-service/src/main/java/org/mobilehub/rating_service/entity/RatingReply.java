package org.mobilehub.rating_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "rating_replies",
        indexes = {@Index(name = "idx_reply_rating", columnList = "rating_id")})
public class RatingReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id", nullable = false, unique = true)
    private Rating rating;


    @Column(name = "admin_id", nullable = false)
    private Long adminId;


    @Column(name = "admin_name")
    private String adminName;


    @Column(name = "content", length = 2000, nullable = false)
    private String content;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}