package com.ssafy.yumTree.quiz.dao;

import com.ssafy.yumTree.quiz.domain.QuizRoom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuizRoomDao {
    List<QuizRoom> findAll();
    QuizRoom findById(int id);
    void save(QuizRoom quizRoom);
    void delete(QuizRoom quizRoom);
}
