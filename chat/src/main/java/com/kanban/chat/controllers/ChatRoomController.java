package com.kanban.chat.controllers;

import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.models.entities.ChatNotificationEntity;
import com.kanban.chat.services.ChatRoomService;
import com.kanban.chat.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-room")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getChatRoom() {
        return ResponseEntity.ok().body(chatRoomService.findAll());
    }

    @PostMapping("/ok")
    public ResponseEntity<String> ok(@RequestBody ChatNotificationEntity request) {
        notificationService.sendPushNotification(request);
        return ResponseEntity.ok().body("ok");
    }
}
