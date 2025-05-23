package com.ssafy.yumTree.quiz.service;

import com.ssafy.yumTree.quiz.domain.QuizRoom;
import com.ssafy.yumTree.quiz.dto.CreateRoomDto;

import java.util.List;

public interface QuizService {
    List<QuizRoom> getAllRooms();

    void createRoom(CreateRoomDto createRoomDto);

    void addParticipantRoom(int roomId);
}