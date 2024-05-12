package com.kanban.chat.controllers;

import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.dtos.FullTeamDTO;
import com.kanban.chat.services.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{teamId}")
    public ResponseEntity<FullTeamDTO> finChatRoomById(@PathVariable String teamId) {
        return ResponseEntity.ok().body(chatRoomService.findChatRoomById(teamId));
    }

}
