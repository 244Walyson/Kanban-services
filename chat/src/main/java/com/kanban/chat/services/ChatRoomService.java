package com.kanban.chat.services;

import com.kanban.chat.dtos.ChatRoomDTO;
import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoomEntity findChatRoomById(String id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    @Transactional
    public ChatRoomEntity saveMessage(ChatMessageEntity message, String roomId, String sender) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).get();
        chatRoom.addMessage(new ChatMessageEmbedded(message));
        log.info("SERVICE");
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatRoomEntity getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId).get();
    }

    public List<ChatRoomDTO> findAll() {
        List<ChatRoomDTO> chatRooms = chatRoomRepository.findAll().stream().map(ChatRoomDTO::new).toList();
        return chatRooms;
    }
}
