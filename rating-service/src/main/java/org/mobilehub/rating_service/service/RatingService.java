package org.mobilehub.rating_service.service;

import org.mobilehub.rating_service.dto.request.RatingCreateRequest;
import org.mobilehub.rating_service.dto.request.ReplyRequest;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.entity.Rating;
import org.mobilehub.rating_service.entity.RatingReply;
import org.mobilehub.rating_service.mapper.RatingMapper;
import org.mobilehub.rating_service.repository.RatingReplyRepository;
import org.mobilehub.rating_service.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {
    private final RatingRepository ratingRepo;
    private final RatingReplyRepository replyRepo;
    private final RatingMapper mapper;


    public RatingService(RatingRepository ratingRepo, RatingReplyRepository replyRepo, RatingMapper mapper) {
        this.ratingRepo = ratingRepo; this.replyRepo = replyRepo; this.mapper = mapper;
    }


    @Transactional
    public RatingResponse create(RatingCreateRequest req) {
        Rating rating = mapper.toEntity(req);
        rating = ratingRepo.save(rating);
        return mapper.toDto(rating, null);
    }


    @Transactional
    public RatingResponse upsertAdminReply(Long ratingId, ReplyRequest body) {
        Rating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found: " + ratingId));


        RatingReply reply = replyRepo.findByRating_Id(ratingId).orElseGet(RatingReply::new);
        reply.setRating(rating);
        reply.setContent(body.content());
        if (body.adminId() != null) reply.setAdminId(body.adminId());
        if (body.adminName() != null) reply.setAdminName(body.adminName());
        reply = replyRepo.save(reply);
        return mapper.toDto(rating, reply);
    }
}