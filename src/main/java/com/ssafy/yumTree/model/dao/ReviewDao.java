package com.ssafy.yumTree.model.dao;

import com.ssafy.yumTree.model.dto.CommunityDto;
import com.ssafy.yumTree.model.dto.ReviewDto;

import java.util.List;

public interface ReviewDao {
    void insert(ReviewDto reviewDto);
    ReviewDto getReview(int rvNumber);
    List<ReviewDto> getAllReview(int rvCmNumber);
    void update(ReviewDto reviewDto);
    void delete(int rvNumber);
}
