package com.ssafy.yumTree.quiz.service;

import com.ssafy.yumTree.jwt.UserUtil;
import com.ssafy.yumTree.quiz.dao.ParticipantsDao;
import com.ssafy.yumTree.quiz.dao.QuizRoomDao;
import com.ssafy.yumTree.quiz.domain.Participant;
import com.ssafy.yumTree.quiz.domain.QuizRoom;
import com.ssafy.yumTree.quiz.dto.CreateRoomDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRoomDao quizRoomDao;
    private final ParticipantsDao participantsDao;
    private final UserUtil userUtil;

    public QuizServiceImpl(QuizRoomDao quizRoomDao, ParticipantsDao participantsDao, UserUtil userUtil) {
        this.quizRoomDao = quizRoomDao;
        this.participantsDao = participantsDao;
        this.userUtil = userUtil;
    }

    /**
     * 퀴즈방 조회
     * @return List<QuizRoom></>
     */
    @Override
    public List<QuizRoom> getAllRooms() {
        List<QuizRoom> list = quizRoomDao.findAll();
        for (QuizRoom room : list) {
            room.setPassword(null);
        }
        return list;
    }

    /**
     * 방 생성, 유저 host 설정, 유저 participant에 추가
     * @param dto
     */
    @Override
    public void createRoom(CreateRoomDto dto) {

        QuizRoom room = QuizRoom.builder()
                .title(dto.getTitle())
                .entryFee(dto.getAdmission())
                .maxParticipants(dto.getMaxParticipants())
                .roomType(dto.getRoomType())
                .password(dto.getPassword())
                .hostId(userUtil.getCurrentUserId())
                .status("WAITING")
                .currentParticipants(1)
                .currentQuestionNumber(0)
                .createdAt(LocalDateTime.now())
                .build();
        quizRoomDao.save(room);

        Participant participant = Participant.builder()
                .roomId(Math.toIntExact(room.getRoomId()))
                .userId(userUtil.getCurrentUserId())
                .score(0)
                .isHost(true)
                .isReady(false)
                .createdAt(LocalDateTime.now())
                .build();
        participantsDao.save(participant);
    }

    public void addParticipantRoom(@RequestParam("roomId") int roomId) {
        Participant participant = Participant.builder()
                .roomId(Math.toIntExact(roomId))
                .userId(userUtil.getCurrentUserId())
                .score(0)
                .isHost(true)
                .isReady(false)
                .createdAt(LocalDateTime.now())
                .build();
        participantsDao.save(participant);
    }
}
