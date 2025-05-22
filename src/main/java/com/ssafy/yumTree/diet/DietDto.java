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
	
	
	
	

}
