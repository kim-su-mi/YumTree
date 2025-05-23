package com.ssafy.yumTree.diet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * 음식 DTO 
 */
public class FoodDto {
	private int foodId;
	private String foodName;
	private double foodKcal;
	private double foodCarbs;
	private double foodProtein;
	private double foodFat;
	private double foodSodium;
	private double foodCholesterol;
	private int foodWeight;
	
	
	public FoodDto() {
		// TODO Auto-generated constructor stub
	}

	public int getFoodId() {
		return foodId;
	}

	public void setFoodId(int foodId) {
		this.foodId = foodId;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public double getFoodKcal() {
		return foodKcal;
	}

	public void setFoodKcal(double foodKcal) {
		this.foodKcal = foodKcal;
	}

	public double getFoodCarbs() {
		return foodCarbs;
	}

	public void setFoodCarbs(double foodCarbs) {
		this.foodCarbs = foodCarbs;
	}

	public double getFoodProtein() {
		return foodProtein;
	}

	public void setFoodProtein(double foodProtein) {
		this.foodProtein = foodProtein;
	}

	public double getFoodFat() {
		return foodFat;
	}

	public void setFoodFat(double foodFat) {
		this.foodFat = foodFat;
	}

	public double getFoodSodium() {
		return foodSodium;
	}

	public void setFoodSodium(double foodSodium) {
		this.foodSodium = foodSodium;
	}

	public double getFoodCholesterol() {
		return foodCholesterol;
	}

	public void setFoodCholesterol(double foodCholesterol) {
		this.foodCholesterol = foodCholesterol;
	}

	public int getFoodWeight() {
		return foodWeight;
	}

	public void setFoodWeight(int foodWeight) {
		this.foodWeight = foodWeight;
	}

	@Override
	public String toString() {
		return "FoodDto [foodName=" + foodName + ", foodKacl=" + foodKcal + ", foodCarbs=" + foodCarbs
				+ ", foodProtein=" + foodProtein + ", foodFat=" + foodFat + ", foodSodium=" + foodSodium
				+ ", foodCholesterol=" + foodCholesterol + ", foodWeight=" + foodWeight + "]";
	}

	public FoodDto(String foodName, double foodKcal, double foodCarbs, double foodProtein, double foodFat,
			double foodSodium, double foodCholesterol, int foodWeight) {
		super();
		this.foodName = foodName;
		this.foodKcal = foodKcal;
		this.foodCarbs = foodCarbs;
		this.foodProtein = foodProtein;
		this.foodFat = foodFat;
		this.foodSodium = foodSodium;
		this.foodCholesterol = foodCholesterol;
		this.foodWeight = foodWeight;
	}

}
