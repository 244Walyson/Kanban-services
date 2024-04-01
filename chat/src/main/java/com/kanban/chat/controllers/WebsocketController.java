package com.kanban.chat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.services.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageEntity chatMessage) throws JsonProcessingException {
        log.info("Message received: " + new ObjectMapper().writeValueAsString(chatMessage));
        chatMessage = chatService.saveChatMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(chatMessage.getReceiver().getUsername(), "/queue/messages", chatMessage);
    }

    @SubscribeMapping("/user2/queue/messages")
    public List<ChatMessageEntity> sendOneTimeMessage() {
           List<ChatMessageEntity> chatMessageEntities = chatService.findAllByReceiver();
        return chatMessageEntities;
    }

}