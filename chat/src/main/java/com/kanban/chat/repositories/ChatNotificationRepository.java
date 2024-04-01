package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatMessageEntity;
import com.kanban.chat.models.entities.ChatNotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatNotificationRepository extends MongoRepository<ChatNotificationEntity, String> {
}
