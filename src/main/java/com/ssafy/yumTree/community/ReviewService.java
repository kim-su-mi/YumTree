package com.ssafy.yumTree.community;


import java.util.List;

public interface ReviewService {
    void writeReview(ReviewDto reviewDto);
    List<ReviewDto> getAllReviewById(int rvCmNumber);
    ReviewDto getReviewById(int rvNumber);
    void updateReview(ReviewDto reviewDto);
    void deleteReview(int rvNumber);
}
