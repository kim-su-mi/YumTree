package com.ssafy.yumTree.diet;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatCompletionDto {
	private String model;
    private List<ChatRequestMsgDto> messages;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public List<ChatRequestMsgDto> getMessages() {
		return messages;
	}
	public void setMessages(List<ChatRequestMsgDto> messages) {
		this.messages = messages;
	}
	public Integer getMaxTokens() {
		return maxTokens;
	}
	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}
    
    
}
