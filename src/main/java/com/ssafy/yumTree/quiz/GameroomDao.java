package com.ssafy.yumTree.quiz;


import java.util.List;

public interface GameroomDao {
    GameroomDto getGame(int gameId);
    List<GameroomDto> getAllGames();
    void createGame(GameroomDto gameDto);
    void deleteGame(int gameId);
    void updateGame(int gameId, GameroomDto gameDto);
}
