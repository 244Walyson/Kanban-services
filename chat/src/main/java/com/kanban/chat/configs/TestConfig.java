package com.kanban.chat.configs;

import java.time.Instant;
import java.util.Arrays;

import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.*;
import com.kanban.chat.repositories.ChatNotificationRepository;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.ChatRoomRepository;
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
  private ChatRoomRepository chatRoomRepository;

  @PostConstruct
  public void init() {
    userRepository.deleteAll();
    chatRepository.deleteAll();
    chatRoomRepository.deleteAll();



    UserEntity user1 = new UserEntity(null, "Maria Brown", "maria2543", "maria@gmail.com", "https://images.unsplash.com/photo-1714060334882-41e92bd8f73b?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwyfHx8ZW58MHx8fHx8", null);
    UserEntity user2 = new UserEntity(null, "Alex Green", "alex321", "alex@gmail.com", "https://images.unsplash.com/photo-1714060334882-41e92bd8f73b?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwyfHx8ZW58MHx8fHx8", null);

    ChatMessageEntity message1 = new ChatMessageEntity(null, new UserEmbedded(user1), "Hello", Instant.now(), MessageStatus.READ);
    ChatMessageEntity message2 = new ChatMessageEntity(null, new UserEmbedded(user2), "Hi",  Instant.now(), MessageStatus.DELIVERED);

    ChatRoomEntity chatRoom = new ChatRoomEntity(null, "team1", "https://img.freepik.com/fotos-gratis/respingo-colorido-abstrato-3d-background-generativo-ai-background_60438-2509.jpg?w=1380&t=st=1713036036~exp=1713036636~hmac=0d9befe3da8d349d81208c42dc7194bec616b25a09d18896efab4022f17bd2e9", "chat2");
    chatRoom.addMember(new UserEmbedded(user1));
    chatRoom.addMember(new UserEmbedded(user2));
    chatRoom.addMessage(new ChatMessageEmbedded(message1));
    chatRoom.addMessage(new ChatMessageEmbedded(message2));
    ChatRoomEntity chatRoom2 = new ChatRoomEntity(null, "team2", "https://img.freepik.com/fotos-gratis/respingo-colorido-abstrato-3d-background-generativo-ai-background_60438-2509.jpg?w=1380&t=st=1713036036~exp=1713036636~hmac=0d9befe3da8d349d81208c42dc7194bec616b25a09d18896efab4022f17bd2e9", "chat2");

    user1.addChatRoomEntity(chatRoom);
    user2.addChatRoomEntity(chatRoom);

    chatRoomRepository.save(chatRoom);
    chatRepository.saveAll(Arrays.asList(message1, message2));
    userRepository.saveAll(Arrays.asList(user1, user2));
  }
}
