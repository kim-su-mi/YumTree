package com.ssafy.yumTree.diet;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DietDao {

	int insertFood(FoodDto foodDto);

	List<Map<String, Object>> getDietSummaryByMonth(Map<String, Object> map);
	
	 /**
     * 음식 이름으로 검색 (LIKE 검색)
     */
    List<FoodDto> findFoodsByName(String foodName);
    
    /**
     * 음식 ID로 단일 조회
     */
    FoodDto findFoodById(int foodId);

	

}
