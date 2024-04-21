package com.kanban.chat.services;

import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatNotificationEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;


    public ChatRoomEntity findChatRoomById(String id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    @Transactional
    public ChatMessageEntity saveMessage(ChatMessageEntity message, String roomId, String sender) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).get();
        UserEntity user = userService.getLoggedUser(sender);
        message.setSender(new UserEmbedded(user));
        message.setInstant(Instant.now());
        ChatMessageEmbedded chatMessageEmbedded = new ChatMessageEmbedded(message);
        chatRoom.addMessage(chatMessageEmbedded);
        chatRoomRepository.save(chatRoom);

        notificationService.sendNotification(roomId, message.getContent(), user);

        return message;
    }


    @Transactional
    public ChatRoomEntity getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId).get();
    }

    public List<ChatRoomDTO> findAll() {
        List<ChatRoomDTO> chatRooms = chatRoomRepository.findAll().stream().map(ChatRoomDTO::new).toList();
        return chatRooms;
    }

    public List<ChatRoomEntity> findAllByUserNick(String nickName) {
        return chatRoomRepository.findAllByMembersNickName(nickName);
    }
}
