package com.kanban.chat.configs;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.*;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;

@Configuration
@Profile("test")
public class TestConfig {

  @Autowired
  private ChatRepository chatRepository;
  @Autowired
  private MessageRepository messageRepository;

  @PostConstruct
  public void init() {
    chatRepository.deleteAll();
    messageRepository.deleteAll();

    UserEmbedded user1 = UserEmbedded.builder()
            .nickname("user1")
            .name("User 1")
            .imgUrl("https://randomuser.me/api/portraits")
            .build();
    UserEmbedded user2 = UserEmbedded.builder()
            .nickname("user2")
            .name("User 2")
            .imgUrl("https://randomuser.me/api/portraits")
            .build();

    Chat chat1 = Chat.builder()
            .roomName("Room 1")
            .description("Description 1")
            .imgUrl("https://randomuser.me/api/portraits")
            .members(Arrays.asList(user1, user2))
            .latestMessage("Message 1")
            .latestActivity(new Date())
            .build();

    chatRepository.save(chat1);
    Message message1 = Message.builder()
            .chat(chat1)
            .content("Message 1")
            .instant(new Date())
            .sender(user1)
            .status(MessageStatus.SENT)
            .build();
    messageRepository.save(message1);
  }
}
