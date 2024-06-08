package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends MongoRepository<UserEntity, String> {


    UserEntity findByNickname(String nickname);

    List<UserEntity> findByChatRoomEntityId(String id);

}
