package com.kanban.chat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.services.AuthService;
import com.kanban.chat.services.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ok")
public class Controllers {

  @Autowired
  private ChatService chatService;
 @Autowired
 private AuthService authService;
 @Autowired
 private ChatRoomRepository chatRoomRepository;

  @GetMapping
  public String ok() {
    return "ok";
  }
  @GetMapping("/chat/{id}")
  public ChatRoomEntity chat(@PathVariable String id) throws JsonProcessingException {
      ChatRoomEntity entity = chatRoomRepository.checkIfUserIsMember("maria2543", id);
      log.info(entity.toString());
      return entity;
  }
}
