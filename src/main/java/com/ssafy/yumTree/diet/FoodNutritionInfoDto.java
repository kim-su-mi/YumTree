package com.ssafy.yumTree.diet;

public class FoodNutritionInfoDto {
	 private int foodId;
	    private String foodName;
	    private double foodKcal;
	    private double foodCarbs;
	    private double foodProtein;
	    private double foodFat;
	    private double foodSodium;
	    private double foodCholesterol;
	    private double foodWeight;
	    private int analyzedWeight; // GPT가 분석한 용량
	    private double calculatedKcal; // 용량에 맞게 계산된 칼로리
	    private double calculatedCarbs;
	    private double calculatedProtein;
	    private double calculatedFat;
	    private double calculatedSodium;
	    private double calculatedCholesterol;
	    
	    public FoodNutritionInfoDto() {
			// TODO Auto-generated constructor stub
		}

		public FoodNutritionInfoDto(int foodId, String foodName, double foodKcal, double foodCarbs, double foodProtein,
				double foodFat, double foodSodium, double foodCholesterol, double foodWeight, int analyzedWeight,
				double calculatedKcal, double calculatedCarbs, double calculatedProtein, double calculatedFat,
				double calculatedSodium, double calculatedCholesterol) {
			super();
			this.foodId = foodId;
			this.foodName = foodName;
			this.foodKcal = foodKcal;
			this.foodCarbs = foodCarbs;
			this.foodProtein = foodProtein;
			this.foodFat = foodFat;
			this.foodSodium = foodSodium;
			this.foodCholesterol = foodCholesterol;
			this.foodWeight = foodWeight;
			this.analyzedWeight = analyzedWeight;
			this.calculatedKcal = calculatedKcal;
			this.calculatedCarbs = calculatedCarbs;
			this.calculatedProtein = calculatedProtein;
			this.calculatedFat = calculatedFat;
			this.calculatedSodium = calculatedSodium;
			this.calculatedCholesterol = calculatedCholesterol;
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

		public double getFoodWeight() {
			return foodWeight;
		}

		public void setFoodWeight(double foodWeight) {
			this.foodWeight = foodWeight;
		}

		public int getAnalyzedWeight() {
			return analyzedWeight;
		}

		public void setAnalyzedWeight(int analyzedWeight) {
			this.analyzedWeight = analyzedWeight;
		}

		public double getCalculatedKcal() {
			return calculatedKcal;
		}

		public void setCalculatedKcal(double calculatedKcal) {
			this.calculatedKcal = calculatedKcal;
		}

		public double getCalculatedCarbs() {
			return calculatedCarbs;
		}

		public void setCalculatedCarbs(double calculatedCarbs) {
			this.calculatedCarbs = calculatedCarbs;
		}

		public double getCalculatedProtein() {
			return calculatedProtein;
		}

		public void setCalculatedProtein(double calculatedProtein) {
			this.calculatedProtein = calculatedProtein;
		}

		public double getCalculatedFat() {
			return calculatedFat;
		}

		public void setCalculatedFat(double calculatedFat) {
			this.calculatedFat = calculatedFat;
		}

		public double getCalculatedSodium() {
			return calculatedSodium;
		}

		public void setCalculatedSodium(double calculatedSodium) {
			this.calculatedSodium = calculatedSodium;
		}

		public double getCalculatedCholesterol() {
			return calculatedCholesterol;
		}

		public void setCalculatedCholesterol(double calculatedCholesterol) {
			this.calculatedCholesterol = calculatedCholesterol;
		}

		@Override
		public String toString() {
			return "FoodNutritionInfoDto [foodId=" + foodId + ", foodName=" + foodName + ", foodKcal=" + foodKcal
					+ ", foodCarbs=" + foodCarbs + ", foodProtein=" + foodProtein + ", foodFat=" + foodFat
					+ ", foodSodium=" + foodSodium + ", foodCholesterol=" + foodCholesterol + ", foodWeight="
					+ foodWeight + ", analyzedWeight=" + analyzedWeight + ", calculatedKcal=" + calculatedKcal
					+ ", calculatedCarbs=" + calculatedCarbs + ", calculatedProtein=" + calculatedProtein
					+ ", calculatedFat=" + calculatedFat + ", calculatedSodium=" + calculatedSodium
					+ ", calculatedCholesterol=" + calculatedCholesterol + "]";
		}
	    
		
		
	    

}
