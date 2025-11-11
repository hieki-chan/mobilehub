package org.mobilehub.rating_service.repository;

import org.mobilehub.rating_service.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByProductId(Long productId, Pageable pageable);
    Page<Rating> findByCreatedAtBetween(Instant from, Instant to, Pageable pageable);
}