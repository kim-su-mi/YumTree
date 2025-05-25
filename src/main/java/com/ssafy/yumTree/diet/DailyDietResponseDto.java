package com.ssafy.yumTree.diet;

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
public class DailyDietResponseDto {
	private boolean success;
    private DailyDietDataDto data;

}
