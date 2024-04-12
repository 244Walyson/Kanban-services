package com.kanban.chat.controllers;

import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.services.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat-room")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getChatRoom() {
        return ResponseEntity.ok().body(chatRoomService.findAll());
    }
}
