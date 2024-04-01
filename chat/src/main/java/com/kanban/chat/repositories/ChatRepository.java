package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRepository extends MongoRepository<ChatMessageEntity, String> {

    @Query("{'receiver.username': ?0}")
    List<ChatMessageEntity> findAllByReceiverUsername(String nickname);
}
