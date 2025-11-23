package org.mobilehub.rating_service.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AllArgsConstructor;
import org.mobilehub.rating_service.client.UserClient;
import org.mobilehub.rating_service.dto.request.RatingCreateRequest;
import org.mobilehub.rating_service.dto.request.RatingUpdateRequest;
import org.mobilehub.rating_service.dto.request.ReplyRequest;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.entity.Rating;
import org.mobilehub.rating_service.entity.RatingReply;
import org.mobilehub.rating_service.mapper.RatingMapper;
import org.mobilehub.rating_service.repository.RatingReplyRepository;
import org.mobilehub.rating_service.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingReplyRepository ratingReplyRepository;
    private final RatingMapper mapper;
    private final UserClient userClient;

    @Transactional
    public RatingResponse create(RatingCreateRequest req) {
        Rating rating = mapper.toEntity(req);
        rating = ratingRepository.save(rating);
        String username = userClient.getUserName(rating.getUserId());
        return mapper.toRatingDto(rating, username, null);
    }

    @Transactional
    public RatingResponse update(RatingUpdateRequest req) {
        Rating rating = ratingRepository.findById(req.ratingId())
                .orElseThrow(() -> new RuntimeException("lmao"));
        rating.setComment(req.comment());
        rating.setStars(req.stars());
        rating.setUpdatedAt(Instant.now());
        rating = ratingRepository.save(rating);
        String username = userClient.getUserName(rating.getUserId());
        return mapper.toRatingDto(rating, username, null);
    }

    public RatingResponse getRatingOfProduct(Long userId, Long productId)
    {
        Rating rating = ratingRepository.findByUserIdAndProductId(userId, productId)
                .orElse(null);
        if(rating == null)
            return null;
        String username = userClient.getUserName(rating.getUserId());
        return mapper.toRatingDto(rating, username, null);
    }

    @Transactional
    public RatingResponse upsertAdminReply(Long ratingId, ReplyRequest body) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found: " + ratingId));


        RatingReply reply = ratingReplyRepository.findByRatingId(ratingId).orElseGet(RatingReply::new);
        reply.setRating(rating);
        reply.setContent(body.content());
        if (body.adminId() != null) reply.setAdminId(body.adminId());
        reply = ratingReplyRepository.save(reply);
        String username = userClient.getUserName(rating.getUserId());
        return mapper.toRatingDto(rating, username, reply);
    }

    @Transactional
    public RatingResponse deleteReply(Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found: " + ratingId));

        ratingReplyRepository.findByRatingId(ratingId).ifPresent(ratingReplyRepository::delete);

        String username = userClient.getUserName(rating.getUserId());
        return mapper.toRatingDto(rating, username, null);
    }

    // ADMIN
    public Page<RatingResponse> getRatings(
            Long productId,
            String stars,
            String replyStatus,
            String searchBy,
            String searchQuery,
            String sortBy,
            int page,
            int size
    ) {
        Integer starInt;
        if (stars != null && !stars.equalsIgnoreCase("ALL")) {
            starInt = Integer.valueOf(stars);
        } else {
            starInt = null;
        }

        Specification<Rating> spec = (root, query, cb) -> cb.conjunction();

        if (productId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("productId"), productId));
        }

        if (starInt != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("stars"), starInt));
        }

        if (replyStatus != null && !replyStatus.equalsIgnoreCase("ALL")) {
            spec = spec.and((root, query, cb) -> {
                assert query != null;
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<RatingReply> replyRoot = subquery.from(RatingReply.class);
                subquery.select(replyRoot.get("rating").get("id"))
                        .where(cb.isNotNull(replyRoot.get("content")));

                if (replyStatus.equalsIgnoreCase("REPLIED")) {
                    return root.get("id").in(subquery);
                } else { // UNREPLIED
                    return cb.not(root.get("id").in(subquery));
                }
            });
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = "%" + searchQuery.trim().toLowerCase() + "%";
            if ("user".equalsIgnoreCase(searchBy)) {
                spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("user").get("name")), q));
            } else {
                spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("product").get("name")), q));
            }
        }

        Pageable pageable = PageRequest.of(page, size, getSort(sortBy));
        Page<Rating> ratingPage = ratingRepository.findAll(spec, pageable);

        return ratingPage.map(r -> {
            String username = userClient.getUserName(r.getUserId());
            return mapper.toRatingDto(r, username, r.getReply());
        });
    }

    private Sort getSort(String sortBy) {
        return switch (sortBy) {
            case "oldest" -> Sort.by("createdAt").ascending();
            case "highest" -> Sort.by("stars").descending();
            case "lowest" -> Sort.by("stars").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }
}