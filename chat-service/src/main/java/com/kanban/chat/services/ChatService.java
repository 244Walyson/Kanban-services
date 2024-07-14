package com.kanban.chat.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.dtos.*;
import com.kanban.chat.exceptions.ResourceNotFoundException;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.Chat;
import com.kanban.chat.models.entities.Message;
import com.kanban.chat.models.entities.MessageStatus;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.MessageRepository;
import com.kanban.chat.services.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class ChatService {

  private final ChatRepository chatRepository;
  private final MessageRepository messageRepository;
  private final KafkaProducer kafkaProducer;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatService(ChatRepository chatRepository, KafkaProducer kafkaProducer, MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
    this.chatRepository = chatRepository;
    this.kafkaProducer = kafkaProducer;
    this.messageRepository = messageRepository;
    this.messagingTemplate = messagingTemplate;
  }

  @Transactional
  public MessageDTO saveMessage(MessageDTO messageDTO, String roomId, String sender) {
    Chat chat = chatRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
    UserEmbedded userEmbedded = chat.getMembers().stream()
            .filter(m -> m.getNickname().equals(sender)).findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Message message = Message.builder()
            .chat(chat)
            .content(messageDTO.getContent())
            .instant(new Date())
            .sender(userEmbedded)
            .status(MessageStatus.SENT)
            .build();
    chat.setLatestMessage(messageDTO.getContent());
    chat.setLatestActivity(new Date());
    chatRepository.save(chat);
    sendMessagesToMembers(chat);
    return MessageDTO.of(messageRepository.save(message));
  }

  private void sendMessagesToMembers(Chat chat) {
    chat.getMembers().forEach(member -> {
      messagingTemplate.convertAndSendToUser(member.getNickname(), "/queue/chats", List.of(ChatListDTO.of(chat)));
      log.info("Message sent to: " + member.getNickname());
    });
  }

  @Transactional(readOnly = true)
  public ChatDTO findChatById(String roomId) {
    Chat chat = chatRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));
    List<MessageDTO> messagesDTO = messageRepository.findByChat(chat)
            .stream().map(MessageDTO::of).toList();
    ChatDTO chatDTO = ChatDTO.of(chat);
    chatDTO.setMessages(messagesDTO);
    return chatDTO;
  }

  @Transactional(readOnly = true)
  public List<ChatListDTO> findAllByUserNick(String nickname) {
    var chats = new ArrayList<>(chatRepository.findAllByMembersNickname(nickname));
    var chatDTOs = filterChatList(chats, nickname);
    chatDTOs.sort(Comparator.comparing(ChatListDTO::getLastActivity).reversed());
    return chatDTOs;
  }

  private List<ChatListDTO> filterChatList(List<Chat> chats, String nickname) {
    var chatDTOs = new ArrayList<ChatListDTO>();
    chats.forEach(c ->
    {
      var chatDTO = ChatListDTO.of(c);
      if (c.getId().contains("U")) {
        if (c.getMembers().get(0).getNickname().equals(nickname)) {
          chatDTO.setRoomName(c.getMembers().get(1).getName());
          chatDTO.setImgUrl(c.getMembers().get(1).getImgUrl());
          chatDTOs.add(chatDTO);
        } else {
          chatDTO.setRoomName(c.getMembers().get(0).getName());
          chatDTO.setImgUrl(c.getMembers().get(0).getImgUrl());
          chatDTOs.add(chatDTO);
        }
      }
      chatDTOs.add(chatDTO);
    });
    return chatDTOs;
  }

  private void sendKafkaNotification(NotificationDTO notification) {
    try {
      kafkaProducer.sendChatMessageNotification(new ObjectMapper().writeValueAsString(notification));
    } catch (Exception e) {
      log.error("Error sending event topic with data {} ", notification);
    }
  }
}
