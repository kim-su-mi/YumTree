package com.ssafy.yumTree.community;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface ReviewDao {
    void insert(ReviewDto reviewDto);
    ReviewDto getReview(int rvNumber);
    List<ReviewDto> getAllReview(int rvCmNumber);
    void update(ReviewDto reviewDto);
    void delete(int rvNumber);
}
