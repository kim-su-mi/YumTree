package com.ssafy.yumTree.diet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzedFoodDto {
	private String name;
    private int weight;
    
    public AnalyzedFoodDto() {
		// TODO Auto-generated constructor stub
	}

	public AnalyzedFoodDto(String name, int weight) {
		super();
		this.name = name;
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "AnalyzedFoodDto [name=" + name + ", weight=" + weight + "]";
	}
    
    

}
