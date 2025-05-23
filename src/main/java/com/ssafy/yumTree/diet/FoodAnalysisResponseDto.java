package com.ssafy.yumTree.diet;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * gpt한테 응답 받은 값 저장하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class FoodAnalysisResponseDto {
	private List<AnalyzedFoodDto> foods;

	public List<AnalyzedFoodDto> getFoods() {
		return foods;
	}

	public void setFoods(List<AnalyzedFoodDto> foods) {
		this.foods = foods;
	}

	public FoodAnalysisResponseDto() {
		// TODO Auto-generated constructor stub
	}
	public FoodAnalysisResponseDto(List<AnalyzedFoodDto> foods) {
		super();
		this.foods = foods;
	}

	@Override
	public String toString() {
		return "FoodAnalysisResponseDto [foods=" + foods + "]";
	}
	
	

}
