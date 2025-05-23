package com.ssafy.yumTree.diet;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequestMsgDto {
	 private String role;
	    private List<ContentDto> content; // String이 아닌 List<ContentDto>
    
    
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<ContentDto> getContent() {
		return content;
	}
	public void setContent(List<ContentDto> content) {
		this.content = content;
	}
    
    
}