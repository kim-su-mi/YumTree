package com.ssafy.yumTree.diet;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MealDto {
	private NutritionDto totalNutrition;
    private List<FoodItemDto> foods;

}
