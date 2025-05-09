package com.ssafy.yumTree.quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ParticipantDto {
    private int participantId;
    private int gameId;
    private String nickname;
    private int score;
    private int rank;
    private boolean connected;
    private Date createdAt;
}
