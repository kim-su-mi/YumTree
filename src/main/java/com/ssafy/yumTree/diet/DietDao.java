package com.ssafy.yumTree.diet;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DietDao {

	int insertFood(FoodDto foodDto);

	List<Map<String, Object>> getDietSummaryByMonth(Map<String, Object> map);

	

}
