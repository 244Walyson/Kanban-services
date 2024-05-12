package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatUserRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatUserRepository extends MongoRepository<ChatUserRoomEntity, String> {
}
