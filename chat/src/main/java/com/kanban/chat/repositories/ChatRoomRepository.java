package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatRoomRepository extends MongoRepository<ChatRoomEntity, String> {

    @Query(value = "{'users.username': ?0}", exists = true)
    boolean checkIfUserIsMember(String username);
}
