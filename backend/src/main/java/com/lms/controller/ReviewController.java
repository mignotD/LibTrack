package com.lms.controller;

import com.lms.dto.request.ReviewRequest;
import com.lms.dto.response.ApiResponse;
import com.lms.dto.response.ReviewResponse;
import com.lms.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/book/{isbn}")
    public ResponseEntity<List<ReviewResponse>> getBookReviews(@PathVariable String isbn) {
        return ResponseEntity.ok(reviewService.getByBook(isbn));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @RequestAttribute("memberId") Integer memberId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(
                reviewService.addReview(request.getIsbn(), memberId, request.getRating(), request.getComment()));
    }
}
