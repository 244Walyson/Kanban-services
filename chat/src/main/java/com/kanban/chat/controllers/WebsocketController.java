package com.kanban.chat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.services.AuthService;
import com.kanban.chat.services.ChatService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;


@Slf4j
@Controller
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthService authService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageEntity chatMessage) throws JsonProcessingException {
        log.info("Message received: " + new ObjectMapper().writeValueAsString(chatMessage));
        chatMessage = chatService.saveChatMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(chatMessage.getReceiver().getNickName(), "/queue/messages", chatMessage);
    }

    //@PreAuthorize("@authService.isMemberOfChat()")
    @MessageMapping("/chat/{roomId}")
    public void processMessage(@Payload ChatMessageEntity chatMessage,
                               @DestinationVariable String roomId,
                               @Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) {
       String nickName = String.valueOf(sessionAttributes.get("nickName"));
        log.info(nickName);
        log.info(String.valueOf(authService.isMemberOfChat(nickName, roomId)));
        messagingTemplate.convertAndSend("/chat/" + roomId, chatMessage);
    }

    @SubscribeMapping("/user2/queue/messages")
    public List<ChatMessageEntity> sendOneTimeMessage() {
           List<ChatMessageEntity> chatMessageEntities = chatService.findAllByReceiver();
        return chatMessageEntities;
    }

    @SubscribeMapping("/chat/{roomId}")
    public ChatMessageEntity sendOneTimeMessage(@DestinationVariable String roomId) {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setContent("Welcome to room " + roomId);
        return chatMessageEntity;
    }


}