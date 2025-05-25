package com.ssafy.yumTree.diet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

public interface DietService {
	int addFood(FoodDto foodDto);

	MonthlyDietSummaryResponseDto getMonthlySummary(String date);

	String imageUpload(MultipartFile file) throws IOException;

	/**
     * ChatGPT API 호출
     */

	Map<String, Object> promptWithMap(Map<String, Object> requestMap);
	 Map<String, Object> analyzeFood(MultipartFile file) throws Exception;

	List<FoodDto> getSearchFood(String search);

	// 일일 식단 조회
    DailyDietResponseDto getDailyDiet(String dateStr);
    
    // 특정 날짜의 특정 음식 상세 정보 조회
    FoodDetailResponseDto getFoodDetailByDate(String dateStr, int foodId);

}
