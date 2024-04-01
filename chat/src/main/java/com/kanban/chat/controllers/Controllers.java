package com.kanban.chat.controllers;

import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ok")
public class Controllers {

  @Autowired
  private ChatService chatService;

  @GetMapping
  public String ok() {
    return "ok";
  }
  @GetMapping("/chat")
  public List<ChatMessageEntity> chat() {
    return chatService.findAllByReceiver();
  }
}
