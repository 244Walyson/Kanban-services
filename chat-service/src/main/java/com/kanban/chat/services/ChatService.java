package com.kanban.chat.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.*;
import com.kanban.chat.repositories.ChatRepository;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.ChatUserRepository;
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
    private ChatUserRepository chatUserRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Transactional
    public ChatMessageEntity saveChatMessage(ChatMessageEntity chatMessageEntity, String sender, String roomId) {
        return null;
    }

    public void sendToChatMembers(String roomId, String nickname) {
        log.info("Sending message to chat members");
        if (roomId.contains("U")) {
            ChatUserRoomEntity chatUserRoomEntity = chatUserRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Chat room not found"));
            log.info("Roooom");
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
            chatRoomDTO.setId(chatUserRoomEntity.getId());
            chatRoomDTO.setLatestMessage(chatUserRoomEntity.getLatestMessage());
            chatRoomDTO.setLastActivity(chatUserRoomEntity.getLastActivity());
            if (nickname.equals(chatUserRoomEntity.getUser1().getNickname())) {
                chatRoomDTO.setRoomName(chatUserRoomEntity.getUser1().getName());
                chatRoomDTO.setImgUrl(chatUserRoomEntity.getUser1().getImgUrl());
                messagingTemplate.convertAndSendToUser(chatUserRoomEntity.getUser2().getNickname(), "/queue/chats", List.of(chatRoomDTO));
                log.info("Message sent to: " + nickname);
                try {
                    log.info(new ObjectMapper().writeValueAsString(chatRoomDTO));
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            chatRoomDTO.setRoomName(chatUserRoomEntity.getUser2().getName());
            chatRoomDTO.setImgUrl(chatUserRoomEntity.getUser2().getImgUrl());
            messagingTemplate.convertAndSendToUser(chatUserRoomEntity.getUser1().getNickname(), "/queue/chats", List.of(chatRoomDTO));
            log.info("Message sent to: " + nickname);
            return;
        }
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Chat room not found"));
        List<UserEmbedded> members = chatRoom.getMembers();
        members.forEach(member -> {
            messagingTemplate.convertAndSendToUser(member.getNickname(), "/queue/chats", Arrays.asList(new ChatRoomDTO(chatRoom)));
            log.info("Message sent to: " + member.getNickname());
        });
    }

}
