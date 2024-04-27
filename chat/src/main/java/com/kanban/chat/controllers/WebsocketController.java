package com.kanban.chat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.dtos.ChatDTO;
import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.services.AuthService;
import com.kanban.chat.services.ChatRoomService;
import com.kanban.chat.services.ChatService;
import lombok.AllArgsConstructor;
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

import java.time.Instant;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Controller
public class WebsocketController {


    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    private final ChatRoomService chatRoomService;

    private final AuthService authService;


    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageEntity chatMessage) throws JsonProcessingException {
        log.info("Message received: " + new ObjectMapper().writeValueAsString(chatMessage));
        //chatMessage = chatService.saveChatMessage(chatMessage);
        messagingTemplate.convertAndSendToUser("user", "/queue/messages", chatMessage);
    }

    //@PreAuthorize("@authService.isMemberOfChat()")
    @MessageMapping("/chat/{roomId}")
    public void processMessage(@Payload ChatMessageEntity chatMessage,
                               @DestinationVariable String roomId,
                               @Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) throws JsonProcessingException {

       String nickName = String.valueOf(sessionAttributes.get("nickName"));

        if(!authService.isMemberOfChat(nickName, roomId)){
            return;
        }
        ChatMessageEntity msgEntity = chatRoomService.saveMessage(chatMessage, roomId, nickName);
        messagingTemplate.convertAndSendToUser(roomId, "/queue/messages", msgEntity);

    }

    @SubscribeMapping("/{roomId}/queue/messages")
    public ChatDTO sendOneTimeMessage(@DestinationVariable String roomId, @Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) {
        String nickName = String.valueOf(sessionAttributes.get("nickName"));
        if(authService.isMemberOfChat(nickName, roomId)){
            return chatRoomService.getChatRoom(roomId);
        }
        log.info("user is not member of chat room");
        return null;
    }

    @SubscribeMapping("/chats")
    public List<ChatRoomDTO> sendOneTimeMessage(@Header("simpSessionAttributes") Map<String, List<String>> sessionAttributes) {
        String nickName = String.valueOf(sessionAttributes.get("nickName"));
        log.info("Messages sent: " + chatRoomService.findAllByUserNick(nickName).size());
        return chatRoomService.findAllByUserNick(nickName);
    }


    //@SubscribeMapping("/*/queue/messages")
    //public List<ChatMessageEntity> sendOneTimeMessage() {
        //List<ChatMessageEntity> chatMessageEntities = chatService.findAllByReceiver();
        //log.info("Messages sent: " + chatMessageEntities.size());
      //  return chatMessageEntities;
    //}
}