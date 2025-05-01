package com.ssafy.yumTree.controller;

import com.ssafy.yumTree.model.dto.ReviewDto;
import com.ssafy.yumTree.model.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yumTree/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Void> insertReview(@RequestBody ReviewDto reviewDto) {
        reviewService.writeReview(reviewDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{rvNumber}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable int rvNumber) {
        return ResponseEntity.ok(reviewService.getReviewById(rvNumber));
    }

    @GetMapping("/community/{cmNumber}")
    public ResponseEntity<List<ReviewDto>> getAllReviews(@PathVariable int cmNumber) {
        return ResponseEntity.ok(reviewService.getAllReviewById(cmNumber));
    }

    @PutMapping("/{rvNumber}")
    public ResponseEntity<Void> updateReview(@PathVariable int rvNumber, @RequestBody ReviewDto reviewDto) {
        reviewDto.setRvNumber(rvNumber);
        reviewService.updateReview(reviewDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{rvNumber}")
    public ResponseEntity<Void> deleteReview(@PathVariable int rvNumber) {
        reviewService.deleteReview(rvNumber);
        return ResponseEntity.ok().build();
    }
}