package com.kanban.chat.controllers;

import com.kanban.chat.dtos.ChatDTO;
import com.kanban.chat.dtos.ChatListDTO;
import com.kanban.chat.dtos.MessageDTO;
import com.kanban.chat.services.AuthService;
import com.kanban.chat.services.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class WebsocketController {


    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final AuthService authService;

    public WebsocketController(SimpMessagingTemplate messagingTemplate, ChatService chatService, AuthService authService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.authService = authService;
    }


    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO chatMessage) {
        messagingTemplate.convertAndSendToUser("user", "/queue/messages", chatMessage);
    }

    @MessageMapping("/chat/{roomId}")
    public void processMessage(@Payload MessageDTO chatMessage,
                               @DestinationVariable String roomId,
                               @Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) {
       String nickName = String.valueOf(sessionAttributes.get("nickname"));
        if(!authService.isMemberOfChat(nickName, roomId)){
            return;
        }
        MessageDTO messageDTO = chatService.saveMessage(chatMessage, roomId, nickName);
        messagingTemplate.convertAndSendToUser(roomId, "/queue/messages", messageDTO);
    }

    @SubscribeMapping("/{roomId}/queue/messages")
    public ChatDTO sendOneTimeMessage(@DestinationVariable String roomId, @Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) {
        String nickName = String.valueOf(sessionAttributes.get("nickname"));
        if(authService.isMemberOfChat(nickName, roomId)){
            return chatService.findChatById(roomId);
        }
        return null;
    }

    @SubscribeMapping("/{nick}/queue/chats")
    public List<ChatListDTO> sendOneTimeMessage(@Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) {
        String nickname = String.valueOf(sessionAttributes.get("nickname"));
        return chatService.findAllByUserNick(nickname);
    }
}