package com.ssafy.yumTree.diet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyDietSummaryDto {
	private String date; // 날짜 (YYYY-MM-DD)
    private boolean hasBreakfast; // 아침 식단 등록 여부
    private boolean hasLunch; // 점심 식단 등록 여부
    private boolean hasDinner; // 저녁 식단 등록 여부
    private boolean hasSnack; // 간식 등록 여부
    
    
    public DailyDietSummaryDto() {
    	super();
    }
    
	public DailyDietSummaryDto(String date, boolean hasBreakfast, boolean hasLunch, boolean hasDinner,
			boolean hasSnack) {
		super();
		this.date = date;
		this.hasBreakfast = hasBreakfast;
		this.hasLunch = hasLunch;
		this.hasDinner = hasDinner;
		this.hasSnack = hasSnack;
	}
	
	@Override
	public String toString() {
		return "DailyDietSummaryDto [date=" + date + ", hasBreakfast=" + hasBreakfast + ", hasLunch=" + hasLunch
				+ ", hasDinner=" + hasDinner + ", hasSnack=" + hasSnack + "]";
	}

    
    

}
