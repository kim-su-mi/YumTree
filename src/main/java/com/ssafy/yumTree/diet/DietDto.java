package com.ssafy.yumTree.diet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DietDto {
	private int dietId;
	private int dietUserNumber;
	private int dietMeaId; //끼니 번호 
	private int dietFoodName; // 음식 이름 
	private int dietDate; //식단등록일 
	private int dietAmount;// 음식량 
	private String dietUnit;// 음식 단위  (반찬그릇,조각,인분,g ) 
	public int getDietId() {
		return dietId;
	}
	public void setDietId(int dietId) {
		this.dietId = dietId;
	}
	public int getDietUserNumber() {
		return dietUserNumber;
	}
	public void setDietUserNumber(int dietUserNumber) {
		this.dietUserNumber = dietUserNumber;
	}
	public int getDietMeaId() {
		return dietMeaId;
	}
	public void setDietMeaId(int dietMeaId) {
		this.dietMeaId = dietMeaId;
	}
	public int getDietFoodName() {
		return dietFoodName;
	}
	public void setDietFoodName(int dietFoodName) {
		this.dietFoodName = dietFoodName;
	}
	public int getDietDate() {
		return dietDate;
	}
	public void setDietDate(int dietDate) {
		this.dietDate = dietDate;
	}
	public int getDietAmount() {
		return dietAmount;
	}
	public void setDietAmount(int dietAmount) {
		this.dietAmount = dietAmount;
	}
	public String getDietUnit() {
		return dietUnit;
	}
	public void setDietUnit(String dietUnit) {
		this.dietUnit = dietUnit;
	}
	
	
	
	

}
