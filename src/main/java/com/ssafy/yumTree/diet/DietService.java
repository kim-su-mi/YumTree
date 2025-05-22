package com.ssafy.yumTree.diet;

import java.util.List;

import org.springframework.web.multipart.MultipartRequest;

public interface DietService {
	int addFood(FoodDto foodDto);

	MonthlyDietSummaryResponseDto getMonthlySummary(String date);

	String imageUpload(MultipartRequest request);

}
