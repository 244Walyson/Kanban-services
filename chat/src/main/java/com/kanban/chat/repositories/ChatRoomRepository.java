package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoomEntity, String> {

    @Query(value = "{ '_id' : ?0, 'members.nickName' : ?1 }", exists = true)
    boolean checkIfUserIsMember(String chatRoomId, String nickName);

    List<ChatRoomEntity> findAllByMembersNickName(String nickName);
}