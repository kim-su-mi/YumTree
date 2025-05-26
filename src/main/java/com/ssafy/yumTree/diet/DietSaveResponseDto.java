package com.ssafy.yumTree.diet;

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
public class DietSaveResponseDto {
	private boolean success;
    private String message;
    private Integer dietLogId;
    
    public DietSaveResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
