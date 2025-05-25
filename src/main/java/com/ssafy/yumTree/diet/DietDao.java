package com.ssafy.yumTree.diet;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DietDao {

	int insertFood(FoodDto foodDto);

	List<Map<String, Object>> getDietSummaryByMonth(Map<String, Object> map);
	
	// 일일 식단 상세 조회
    List<Map<String, Object>> getDailyDietDetails(Map<String, Object> map);
    
    // 특정 날짜의 특정 음식 상세 정보 조회
    Map<String, Object> getFoodDetailByDate(Map<String, Object> map);
	
	 /**
     * 음식 이름으로 검색 (LIKE 검색)
     */
    List<FoodDto> findFoodsByName(String foodName);
    
    /**
     * 음식 ID로 단일 조회
     */
    FoodDto findFoodById(int foodId);

	List<FoodDto> selectFoodList(String search);

	

}
