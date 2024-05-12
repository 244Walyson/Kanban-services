package com.kanban.chat.services;

import com.kanban.chat.dtos.*;
import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;


    public FullTeamDTO findChatRoomById(String id) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(id).orElse(null);
        List<UserDTO> members = userRepository.findByChatRoomEntityId(id).stream().map(UserDTO::new).toList();
        FullTeamDTO dto = new FullTeamDTO(chatRoom);
        dto.setMembers(members);
        return dto;
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
    public ChatDTO getChatRoom(String roomId) {
        ChatDTO chatRoom = new ChatDTO(chatRoomRepository.findById(roomId).get());
        return chatRoom;
    }

    public List<ChatRoomDTO> findAll() {
        List<ChatRoomDTO> chatRooms = chatRoomRepository.findAll().stream().map(ChatRoomDTO::new).toList();
        return chatRooms;
    }

    public List<ChatRoomDTO> findAllByUserNick(String nickName) {
        return chatRoomRepository.findAllByMembersNickName(nickName).stream().map(ChatRoomDTO::new).toList();
    }
}
