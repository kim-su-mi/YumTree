package com.ssafy.yumTree.quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GameroomDto {
    private int gameId;
    private int gameCode;
    private String title;
    private int hostId;
    private int maxParticipants;
    private int actualParticipants;
    private String status;
    private int currentQuestionIndex;
    private int totalQuestions;
    private Date createdAt;
    private Date updateAt;
}
