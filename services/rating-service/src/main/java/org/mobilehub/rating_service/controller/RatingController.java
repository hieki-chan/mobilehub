package org.mobilehub.rating_service.controller;

import jakarta.validation.Valid;
import org.mobilehub.rating_service.dto.request.RatingCreateRequest;
import org.mobilehub.rating_service.dto.response.PageResponse;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.service.RatingQueryService;
import org.mobilehub.rating_service.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;
    private final RatingQueryService queryService;


    public RatingController(RatingService ratingService, RatingQueryService queryService) {
        this.ratingService = ratingService;
        this.queryService = queryService;
    }


    // User creates a rating
    @PostMapping
    public ResponseEntity<RatingResponse> create(@Valid @RequestBody RatingCreateRequest body) {
        return ResponseEntity.ok(ratingService.create(body));
    }


    // GET by product with pagination & special sort
    @GetMapping("/by-product")
    public ResponseEntity<PageResponse<RatingResponse>> byProduct(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(queryService.getByProduct(productId, page, size, sort));
    }


    // GET by date range (inclusive) with pagination & sort
    @GetMapping("/by-date")
    public ResponseEntity<PageResponse<RatingResponse>> byDate(
            @RequestParam Instant from,
            @RequestParam Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(queryService.getByDateRange(from, to, page, size, sort));
    }
}