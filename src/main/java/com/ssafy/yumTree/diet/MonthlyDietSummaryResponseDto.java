package com.ssafy.yumTree.diet;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyDietSummaryResponseDto {
	private String date; // 조회 기준일 (YYYY-MM-DD)
    private List<DailyDietSummaryDto> dailySummaries; // 해당 월의 일별 식단 요약
    
    
    public MonthlyDietSummaryResponseDto() {
    	super();
    }
    
	public MonthlyDietSummaryResponseDto(String date, List<DailyDietSummaryDto> dailySummaries) {
		super();
		this.date = date;
		this.dailySummaries = dailySummaries;
	}
	@Override
	public String toString() {
		return "MonthlyDietSummaryResponseDto [date=" + date + ", dailySummaries=" + dailySummaries + "]";
	}

    
    

}
