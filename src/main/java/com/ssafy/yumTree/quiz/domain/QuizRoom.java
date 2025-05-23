package com.ssafy.yumTree.quiz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRoom {

    private Long roomId;
    private String title;
    private String hostId;
    private int maxParticipants;
    private int currentParticipants;
    private String password;
    private int entryFee;
    private String roomType;
    private String status;
    private int currentQuestionNumber;
    private LocalDateTime createdAt;
}

