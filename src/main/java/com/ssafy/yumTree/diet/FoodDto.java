package com.ssafy.yumTree.diet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * 직접 식단 등록시 사용하는 dto
 */
public class FoodDto {
	private String foodName;
	private double foodKacl;
	private double foodCarbs;
	private double foodProtein;
	private double foodFat;
	private double foodSodium;
	private double foodCholesterol;
	private int foodWeight;
	
	

	@Override
	public String toString() {
		return "FoodDto [foodName=" + foodName + ", foodKacl=" + foodKacl + ", foodCarbs=" + foodCarbs
				+ ", foodProtein=" + foodProtein + ", foodFat=" + foodFat + ", foodSodium=" + foodSodium
				+ ", foodCholesterol=" + foodCholesterol + ", foodWeight=" + foodWeight + "]";
	}

	public FoodDto(String foodName, double foodKacl, double foodCarbs, double foodProtein, double foodFat,
			double foodSodium, double foodCholesterol, int foodWeight) {
		super();
		this.foodName = foodName;
		this.foodKacl = foodKacl;
		this.foodCarbs = foodCarbs;
		this.foodProtein = foodProtein;
		this.foodFat = foodFat;
		this.foodSodium = foodSodium;
		this.foodCholesterol = foodCholesterol;
		this.foodWeight = foodWeight;
	}

}
