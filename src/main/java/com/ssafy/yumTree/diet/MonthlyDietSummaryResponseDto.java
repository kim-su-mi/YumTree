package com.ssafy.yumTree.diet;

import java.util.List;
import java.util.Map;

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
public class MonthlyDietSummaryResponseDto {
	 private String date;
	 private Map<String, DailyDietSummaryDto> dietMap; // List 대신 Map 사용
    
    
   
    

}
