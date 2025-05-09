package com.ssafy.yumTree.community;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yumTree/community/{id}/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Void> insertReview(@PathVariable int id, @RequestBody ReviewDto reviewDto) {
        reviewDto.setRvCmNumber(id);
        reviewService.writeReview(reviewDto);
        return ResponseEntity.ok().build();
    }

    // 단일 댓글
    @GetMapping("/{rvNumber}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable int rvNumber) {
        return ResponseEntity.ok(reviewService.getReviewById(rvNumber));
    }

//  // 댓글 수정
//    @PutMapping("/{rvNumber}")
//    public ResponseEntity<Void> updateReview(@PathVariable int rvNumber, @RequestBody ReviewDto reviewDto) {
//        reviewDto.setRvNumber(rvNumber);
//        reviewService.updateReview(reviewDto);
//        return ResponseEntity.ok().build();
//    }

    // 댓글 삭제
    @DeleteMapping("/{rvNumber}")
    public ResponseEntity<Void> deleteReview(@PathVariable int rvNumber) {
        reviewService.deleteReview(rvNumber);
        return ResponseEntity.ok().build();
    }
}