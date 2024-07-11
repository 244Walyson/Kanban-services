package com.kanban.chat.repositories;

import com.kanban.chat.models.entities.Chat;
import com.kanban.chat.models.entities.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

  List<Message> findByChat(Chat chat);
}
