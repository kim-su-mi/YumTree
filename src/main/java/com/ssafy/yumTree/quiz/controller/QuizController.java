package com.ssafy.yumTree.quiz.controller;

import com.ssafy.yumTree.quiz.dto.CreateRoomDto;
import com.ssafy.yumTree.quiz.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * 퀴즈 방 조회
     * @return List<RoomListResDto></>
     */
    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        System.out.println(1);
        return new ResponseEntity<>(quizService.getAllRooms(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomDto createRoomDto) {
        quizService.createRoom(createRoomDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<?> admitRoom(@PathVariable int roomId) {
        quizService.addParticipantRoom(roomId);
        return ResponseEntity.ok().build();
    }
}
