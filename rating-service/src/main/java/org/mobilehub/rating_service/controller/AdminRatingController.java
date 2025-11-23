package org.mobilehub.rating_service.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mobilehub.rating_service.dto.request.ReplyRequest;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/ratings")
@SuppressWarnings("unused")
public class AdminRatingController {
    private final RatingService ratingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RatingResponse>> getRatings(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "stars", required = false) String stars,
            @RequestParam(value = "replyStatus", required = false) String replyStatus, // ALL / REPLIED / UNREPLIED
            @RequestParam(value = "searchBy", required = false) String searchBy, // product / user
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "sortBy", defaultValue = "newest") String sortBy, // newest / oldest / highest / lowest
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<RatingResponse> ratings = ratingService.getRatings(
                productId, stars, replyStatus, searchBy, searchQuery, sortBy, page, size
        );
        return ResponseEntity.ok(ratings);
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

    @PutMapping("/{ratingId}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RatingResponse> updateReply(
            @PathVariable Long ratingId,
            @Valid @RequestBody ReplyRequest body
    ) {
        return ResponseEntity.ok(ratingService.upsertAdminReply(ratingId, body));
    }

    @DeleteMapping("/{ratingId}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RatingResponse> deleteReply(
            @PathVariable Long ratingId
    ) {
        return ResponseEntity.ok(ratingService.deleteReply(ratingId));
    }
}