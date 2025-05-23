package com.ssafy.yumTree.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomDto {
    private String title;
    private int admission;
    private int maxParticipants;
    private String roomType;
    private String password;
}
