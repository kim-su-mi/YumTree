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
}
