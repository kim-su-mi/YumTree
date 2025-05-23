package com.ssafy.yumTree.quiz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {
    private int participantId;
    private int roomId;
    private String userId;
    private int score;
    private Boolean isHost;
    private Boolean isReady;
    private LocalDateTime createdAt;
}
