package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatRoomRepository extends MongoRepository<ChatRoomEntity, String> {

    @Query(value = "{ '_id' : ?1, 'members.nickname' : ?0 }")
    ChatRoomEntity checkIfUserIsMember(String username, String chatRoomId);
}
