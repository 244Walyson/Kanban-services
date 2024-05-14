package com.kanban.chat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.dtos.*;
import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.models.entities.ChatUserRoomEntity;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.ChatUserRepository;
import com.kanban.chat.repositories.UserRepository;
import com.kanban.chat.services.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatUserRepository chatUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private KafkaProducer kafkaProducer;


    public FullTeamDTO findChatRoomById(String id) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(id).orElse(null);
        List<UserDTO> members = userRepository.findByChatRoomEntityId(id).stream().map(UserDTO::new).toList();
        FullTeamDTO dto = new FullTeamDTO(chatRoom);
        dto.setMembers(members);
        return dto;
    }

    @Transactional
    public ChatMessageEntity saveMessage(ChatMessageEntity message, String roomId, String sender) {
        UserEntity user = userService.getLoggedUser(sender);
        if (roomId.contains("R")) return saveChatRoom(message, chatRoomRepository.findById(roomId).get(), user);
        return saveUserChat(message, chatUserRepository.findById(roomId).get(), user);
    }

    private ChatMessageEntity saveUserChat(ChatMessageEntity message, ChatUserRoomEntity chatUserRoom, UserEntity sender) {
        message.setSender(new UserEmbedded(sender));
        message.setInstant(new Date());
        ChatMessageEmbedded chatMessageEmbedded = new ChatMessageEmbedded(message);
        chatUserRoom.addMessage(chatMessageEmbedded);
        chatUserRoom.setLastActivity(new Date());
        chatUserRoom.setLatestMessage(message.getContent());
        chatUserRoom = chatUserRepository.save(chatUserRoom);


        NotificationDTO notification = new NotificationDTO();
        notification.setSender(new UserDTO(sender));
        UserDTO userDTO = new UserDTO();
        notification.setReceiver(userDTO);
        notification.setTitle(sender.getNickname());
        notification.setMessage(message.getContent());

        if (chatUserRoom.getUser1().getNickname().equals(sender.getNickname())) {
            notification.setReceiver(new UserDTO(userRepository.findByNickname(chatUserRoom.getUser2().getNickname())));
            sendKafkaNotification(notification);
            return message;
        }
        notification.setReceiver(new UserDTO(userRepository.findByNickname(chatUserRoom.getUser1().getNickname())));
        sendKafkaNotification(notification);
        return message;
    }

    private void sendKafkaNotification(NotificationDTO notification) {
        try {
            kafkaProducer.sendChatMessageNotification(new ObjectMapper().writeValueAsString(notification));
        } catch (Exception e) {
            log.error("Error sending event topic with data {} ", notification);
        }
    }

    private ChatMessageEntity saveChatRoom(ChatMessageEntity message, ChatRoomEntity chatRoom, UserEntity sender) {
        message.setSender(new UserEmbedded(sender));
        message.setInstant(new Date());
        ChatMessageEmbedded chatMessageEmbedded = new ChatMessageEmbedded(message);
        chatRoom.addMessage(chatMessageEmbedded);
        chatRoomRepository.save(chatRoom);

        NotificationDTO notification = new NotificationDTO();
        notification.setSender(new UserDTO(sender));

        List<UserEmbedded> members = chatRoom.getMembers();
        for (UserEmbedded member : members) {
            if (!member.getNickname().equals(sender)) {
                UserDTO userDTO = new UserDTO(userRepository.findByNickname(member.getNickname()));
                notification.setReceiver(userDTO);
                notification.setTitle(sender.getNickname());
                notification.setMessage(message.getContent());

                try {
                    kafkaProducer.sendChatMessageNotification(new ObjectMapper().writeValueAsString(notification));
                } catch (Exception e) {
                    log.error("Error sending event topic with data {} ", notification);
                }
            }
        }
        return message;
    }

    @Transactional
    public ChatDTO getChatRoom(String roomId, String nickname) {
        if (roomId.contains("U")) {
            ChatUserRoomEntity chatUserRoom = chatUserRepository.findById(roomId).get();
            ChatDTO chatRoom = new ChatDTO();
            chatRoom.setId(chatUserRoom.getId());
            if (nickname.equals(chatUserRoom.getUser1().getNickname())) {
                chatRoom.setRoomName(chatUserRoom.getUser2().getName());
                chatRoom.setImgUrl(chatUserRoom.getUser2().getImgUrl());
                chatRoom.setDescription(chatUserRoom.getUser2().getNickname());
                chatRoom.setMessages(chatUserRoom.getMessages().stream().map(MessageDTO::new).toList());
                if (chatRoom.getMessages() == null) chatRoom.setMessages(new ArrayList<>());
                try{
                    log.info("Messages: " + new ObjectMapper().writeValueAsString(chatRoom));
                }catch (Exception e) {
                    log.error("Error converting messages to JSON");
                }
                return chatRoom;
            }
            chatRoom.setRoomName(chatUserRoom.getUser1().getName());
            chatRoom.setDescription(chatUserRoom.getUser1().getNickname());
            chatRoom.setImgUrl(chatUserRoom.getUser1().getImgUrl());
            if (chatRoom.getMessages() == null) chatRoom.setMessages(new ArrayList<>());
                try{
                    log.info("Messages: " + new ObjectMapper().writeValueAsString(chatRoom));
                }catch (Exception e) {
                    log.error("Error converting messages to JSON");
                }
            return chatRoom;
        }
        ChatDTO chatRoom = new ChatDTO(chatRoomRepository.findById(roomId).get());
        return chatRoom;
    }

    public List<ChatRoomDTO> findAll() {
        List<ChatRoomDTO> chatRooms = chatRoomRepository.findAll().stream().map(ChatRoomDTO::new).toList();
        return chatRooms;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> findAllByUserNick(String nickname) {
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>(chatRoomRepository.findAllByMembersNickname(nickname).stream().map(ChatRoomDTO::new).toList());
        List<ChatUserRoomEntity> chatUserRoomEntities = chatUserRepository.findAllByNickname(nickname);
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        for (ChatUserRoomEntity chatUserRoomEntity : chatUserRoomEntities) {
            chatRoomDTO.setId(chatUserRoomEntity.getId());
            chatRoomDTO.setLatestMessage(chatUserRoomEntity.getLatestMessage());
            chatRoomDTO.setLastActivity(chatUserRoomEntity.getLastActivity());
            if (nickname.equals(chatUserRoomEntity.getUser1().getNickname())) {
                chatRoomDTO.setRoomName(chatUserRoomEntity.getUser2().getName());
                chatRoomDTO.setImgUrl(chatUserRoomEntity.getUser2().getImgUrl());
                chatRoomDTOS.add(chatRoomDTO);
                continue;
            }
            chatRoomDTO.setRoomName(chatUserRoomEntity.getUser1().getName());
            chatRoomDTO.setImgUrl(chatUserRoomEntity.getUser1().getImgUrl());
            chatRoomDTOS.add(chatRoomDTO);
        }
        chatRoomDTOS.forEach(c ->
        {
            try {
                log.info(new ObjectMapper().writeValueAsString(c));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        chatRoomDTOS.sort(Comparator.comparing(ChatRoomDTO::getLastActivity));
        return chatRoomDTOS;
    }

}
