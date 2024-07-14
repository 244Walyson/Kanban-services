package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {

    @Query(value = "{ '_id' : ?0, 'members.nickname' : ?1 }", exists = true)
    boolean checkIfUserIsMember(String chatRoomId, String nickName);

    List<Chat> findAllByMembersNickname(String nickName);
}
