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
    private List<ContentDto> content;
}