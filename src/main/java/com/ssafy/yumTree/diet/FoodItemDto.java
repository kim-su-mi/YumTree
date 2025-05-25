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
public class FoodItemDto {
	 private int foodId;
	    private String foodName;
	    private int dietAmount;
	    private String dietUnit;
	    private double actualCalories;
	    private double actualProtein;
	    private double actualFat;
	    private double actualCarbs;
	    private double actualSodium;
	    private double actualCholesterol;
	    private int foodWeight;

}
