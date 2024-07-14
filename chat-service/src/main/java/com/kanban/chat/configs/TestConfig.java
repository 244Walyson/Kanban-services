package com.kanban.chat.configs;

import java.util.Arrays;
import java.util.Date;

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

    // Criar usuários
    UserEmbedded user1 = UserEmbedded.builder()
            .nickname("test1232")
            .name("user test")
            .imgUrl("https://chat-kanban.s3.amazonaws.com/1715623715.jpeg")
            .build();

    UserEmbedded user2 = UserEmbedded.builder()
            .nickname("maria2543")
            .name("Maria Brown")
            .imgUrl("https://chat-kanban.s3.amazonaws.com/1715623715.jpeg")
            .build();

    UserEmbedded user3 = UserEmbedded.builder()
            .nickname("joao123")
            .name("Joao Grey")
            .imgUrl("https://chat-kanban.s3.amazonaws.com/1715623715.jpeg")
            .build();

    UserEmbedded user4 = UserEmbedded.builder()
            .nickname("alex321")
            .name("Alex Grenn")
            .imgUrl("https://chat-kanban.s3.amazonaws.com/1715623715.jpeg")
            .build();

    Chat chat1 = Chat.builder()
            .roomName("Room 1")
            .description("Description 1")
            .imgUrl("https://randomuser.me/api/portraits")
            .members(Arrays.asList(user1, user2, user3, user4))
            .latestMessage("Message 1")
            .latestActivity(new Date())
            .build();

    // Criar chats diretos entre usuários
    Chat directChat1 = Chat.builder()
            .id("U12")
            .roomName("Direct Chat: user test & Maria Brown")
            .description("Direct chat between user test and Maria Brown")
            .imgUrl("https://randomuser.me/api/portraits")
            .members(Arrays.asList(user1, user2))
            .latestMessage("Hello from user test")
            .latestActivity(new Date())
            .build();

    Chat directChat2 = Chat.builder()
            .id("U23")
            .roomName("Direct Chat: Maria Brown & Joao Grey")
            .description("Direct chat between Maria Brown and Joao Grey")
            .imgUrl("https://randomuser.me/api/portraits")
            .members(Arrays.asList(user2, user3))
            .latestMessage("Hello from Maria")
            .latestActivity(new Date())
            .build();

    chatRepository.saveAll(Arrays.asList(directChat1, directChat2, chat1));

    // Criar mensagens para chats diretos
    Message message1 = Message.builder()
            .chat(directChat1)
            .content("Hello from user test")
            .instant(new Date())
            .sender(user1)
            .status(MessageStatus.SENT)
            .build();

    Message message2 = Message.builder()
            .chat(directChat1)
            .content("Hi user test!")
            .instant(new Date())
            .sender(user2)
            .status(MessageStatus.SENT)
            .build();

    Message message3 = Message.builder()
            .chat(directChat2)
            .content("Hello Joao!")
            .instant(new Date())
            .sender(user2)
            .status(MessageStatus.SENT)
            .build();

    messageRepository.saveAll(Arrays.asList(message1, message2, message3));

    // Criar roles
  }
}
