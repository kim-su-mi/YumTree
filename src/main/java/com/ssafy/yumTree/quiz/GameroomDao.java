package com.ssafy.yumTree.quiz;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface GameroomDao {
    GameroomDto getGame(int gameId);
    List<GameroomDto> getAllGames();
    void createGame(GameroomDto gameDto);
    void deleteGame(int gameId);
    void updateGame(int gameId, GameroomDto gameDto);
}
