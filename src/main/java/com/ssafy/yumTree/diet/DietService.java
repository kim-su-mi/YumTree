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

}
