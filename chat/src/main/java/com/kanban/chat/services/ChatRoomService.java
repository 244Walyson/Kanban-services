package com.kanban.chat.services;

import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoomEntity findChatRoomById(String id) {
        return chatRoomRepository.findById(id).orElse(null);
    }
}
