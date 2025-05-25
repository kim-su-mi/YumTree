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
public class NutritionDto {
	private double calories;
    private double protein;
    private double fat;
    private double carbs;

}
