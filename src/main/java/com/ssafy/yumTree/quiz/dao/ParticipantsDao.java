package com.ssafy.yumTree.quiz.dao;

import com.ssafy.yumTree.quiz.domain.Participant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParticipantsDao {
    List<Participant> selectParticipantsByRoomId(int roomId);
    void updateParticipant(Participant participant);
    void save(Participant participant);
    void delete(Participant participant);
}
