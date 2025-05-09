package com.ssafy.yumTree.quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestionDto {
    private int questionId;
    private String content;
    private String answer;
    private String difficulty;
    private Date createdAt;
}
