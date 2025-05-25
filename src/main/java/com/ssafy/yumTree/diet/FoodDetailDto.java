package com.ssafy.yumTree.diet;

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
public class FoodDetailDto {
	private int foodId;
    private String foodName;
    private int amount;
    private String unit;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private double sodium;
    private double cholesterol;
    private int baseWeight; // 1인분 기준 중량

}
