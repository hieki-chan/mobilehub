package org.mobilehub.rating_service.repository;

import org.mobilehub.rating_service.entity.RatingReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingReplyRepository extends JpaRepository<RatingReply, Long> {
    Optional<RatingReply> findByRating_Id(Long ratingId);
}