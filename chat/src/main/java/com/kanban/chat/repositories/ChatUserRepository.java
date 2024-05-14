package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatUserRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatUserRepository extends MongoRepository<ChatUserRoomEntity, String> {

    @Query("{'$or':[{'user1.nickname': ?0}, {'user2.nickname': ?0}]}")
    List<ChatUserRoomEntity> findAllByNickname(String nickName);
}
