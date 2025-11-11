package org.mobilehub.rating_service.service;

import org.mobilehub.rating_service.dto.response.PageResponse;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.entity.Rating;
import org.mobilehub.rating_service.mapper.RatingMapper;
import org.mobilehub.rating_service.repository.RatingReplyRepository;
import org.mobilehub.rating_service.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RatingQueryService {
    private final RatingRepository ratingRepo;
    private final RatingReplyRepository replyRepo;
    private final RatingMapper mapper;


    public RatingQueryService(RatingRepository ratingRepo, RatingReplyRepository replyRepo, RatingMapper mapper) {
        this.ratingRepo = ratingRepo; this.replyRepo = replyRepo; this.mapper = mapper;
    }


    public PageResponse<RatingResponse> getByProduct(Long productId, int page, int size, String sortKey) {
        Pageable pageable = buildSort(page, size, sortKey);
        Page<Rating> p = ratingRepo.findByProductId(productId, pageable);
        List<RatingResponse> items = p.map(r -> mapper.toDto(r, replyRepo.findByRating_Id(r.getId()).orElse(null))).getContent();
        return new PageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }


    public PageResponse<RatingResponse> getByDateRange(Instant from, Instant to, int page, int size, String sortKey) {
        Pageable pageable = buildSort(page, size, sortKey);
        Page<Rating> p = ratingRepo.findByCreatedAtBetween(from, to, pageable);
        List<RatingResponse> items = p.map(r -> mapper.toDto(r, replyRepo.findByRating_Id(r.getId()).orElse(null))).getContent();
        return new PageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }


    private Pageable buildSort(int page, int size, String sortKey) {
        sortKey = (sortKey == null) ? "recent" : sortKey;
        Sort sort = switch (sortKey) {
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "highest" -> Sort.by(Sort.Direction.DESC, "stars").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            case "lowest" -> Sort.by(Sort.Direction.ASC, "stars").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // recent
        };
        return PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1), 100), sort);
    }
}