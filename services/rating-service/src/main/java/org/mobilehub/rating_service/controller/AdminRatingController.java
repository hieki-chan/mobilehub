package org.mobilehub.rating_service.controller;

import jakarta.validation.Valid;
import org.mobilehub.rating_service.dto.request.ReplyRequest;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ratings")
public class AdminRatingController {
    private final RatingService ratingService;


    public AdminRatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }


    // Admin create or update a reply for a rating
    @PostMapping("/{ratingId}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RatingResponse> reply(
            @PathVariable Long ratingId,
            @Valid @RequestBody ReplyRequest body
    ) {
        return ResponseEntity.ok(ratingService.upsertAdminReply(ratingId, body));
    }
}