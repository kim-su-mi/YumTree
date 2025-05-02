package com.ssafy.yumTree.model.service;

import com.ssafy.yumTree.model.dao.ReviewDao;
import com.ssafy.yumTree.model.dto.ReviewDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewDao reviewDao;

    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    @Override
    public void writeReview(ReviewDto reviewDto) {
        reviewDao.insert(reviewDto);
    }

    @Override
    public List<ReviewDto> getAllReviewById(int rvCmNumber) {
        return reviewDao.getAllReview(rvCmNumber);
    }

    @Override
    public ReviewDto getReviewById(int rvNumber) {
        return reviewDao.getReview(rvNumber);
    }

    @Override
    public void updateReview(ReviewDto reviewDto) {
        reviewDao.update(reviewDto);
    }

    @Override
    public void deleteReview(int rvNumber) {
        reviewDao.delete(rvNumber);
    }
}
