package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.ChatRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoomEntity, String> {

    @Query(value = "{ '_id' : ?0, 'members.nickname' : ?1 }", exists = true)
    boolean checkIfUserIsMember(String chatRoomId, String nickName);

    List<ChatRoomEntity> findAllByMembersNickname(String nickName);
}
