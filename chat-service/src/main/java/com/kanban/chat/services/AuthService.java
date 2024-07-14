package com.kanban.chat.services;

import com.kanban.chat.repositories.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("authService")
public class AuthService {

  private final ChatRepository chatRepository;

  public AuthService(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  public boolean isMemberOfChat(String nickName, String chatRoomId) {
    log.info("Checking if user is member of chat {} {}", nickName, chatRoomId);
    return chatRepository.checkIfUserIsMember(chatRoomId, nickName);
  }
}
