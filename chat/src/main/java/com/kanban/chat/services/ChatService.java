package com.kanban.chat.services;

import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.models.entities.MessageStatus;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.UserRepository;
import com.kanban.chat.utils.CustomUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Transactional
    public ChatMessageEntity saveChatMessage(ChatMessageEntity chatMessageEntity, String sender, String roomId) {
        return null;
    }

    public void sendToChatMembers(String roomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new UsernameNotFoundException("Chat room not found"));
        List<UserEmbedded> members = chatRoom.getMembers();
        members.forEach(member -> {
            messagingTemplate.convertAndSendToUser( member.getNickname(), "/queue/chats", Arrays.asList(new ChatRoomDTO(chatRoom)));
            log.info("Message sent to: " + member.getNickname());
        });
    }

}
