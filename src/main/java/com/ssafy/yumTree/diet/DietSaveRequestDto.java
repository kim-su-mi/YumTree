package com.ssafy.yumTree.diet;

import java.util.List;

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
public class DietSaveRequestDto {
	private String dietLogDate;
    private String mealType;
    private String dietImage;
    private List<DietDetailDto> dietDetails;

}
