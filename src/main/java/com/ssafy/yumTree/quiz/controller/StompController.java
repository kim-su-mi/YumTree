package com.ssafy.yumTree.quiz.controller;

import com.ssafy.yumTree.quiz.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;

    public StompController(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    // 방법2. MessageMapping 어노테이션만 활용, 유연하게 사용가능
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Long roomId, ChatMessageDto chatMessageDto) {
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);
    }
}