package com.kanban.chat.configs;

import java.time.Instant;
import java.util.Arrays;

import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatNotificationEntity;
import com.kanban.chat.models.entities.MessageStatus;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatNotificationRepository;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.UserRepository;
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
  private UserRepository userRepository;
  @Autowired
  private ChatNotificationRepository chatNotificationRepository;

  @PostConstruct
  public void init() {
    userRepository.deleteAll();
    chatRepository.deleteAll();


    UserEntity user1 = new UserEntity(null, "Maria Brown", "maria2543", "maria@gmail.com");
    UserEntity user2 = new UserEntity(null, "Alex Green", "alex244", "alex@gmail.com");

    ChatMessageEntity message1 = new ChatMessageEntity(null, new UserEmbedded(user1), new UserEmbedded(user2), "Hello", Instant.now(), MessageStatus.READ);
    ChatMessageEntity message2 = new ChatMessageEntity(null, new UserEmbedded(user2), new UserEmbedded(user1), "Hi",  Instant.now(), MessageStatus.DELIVERED);

    ChatNotificationEntity notification1 = new ChatNotificationEntity(null, user1, user2, "notification");
    ChatNotificationEntity notification2 = new ChatNotificationEntity(null, user2, user1, "notification");

    chatNotificationRepository.saveAll(Arrays.asList(notification1, notification2));
    chatRepository.saveAll(Arrays.asList(message1, message2));
    userRepository.saveAll(Arrays.asList(user1, user2));
  }
}
