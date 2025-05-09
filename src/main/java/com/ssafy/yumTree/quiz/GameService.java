package com.ssafy.yumTree.quiz;


import java.util.List;

public interface GameService {
    // 방 생성
    void createRoom(GameroomDto gameroomDto);

    // 방 1개 조회
    GameroomDto getRoom(int gameId);

    // 전체 방 조회
    List<GameroomDto> getAllRooms();

    // 방 정보 수정
    void updateRoom(int gameId, GameroomDto gameroomDto);

    // 방 삭제
    void deleteRoom(int gameId);

    // ----------------------------------------------------

    // 문제 1개 가져오기


}
