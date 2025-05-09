package com.ssafy.yumTree.model.dto;

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
