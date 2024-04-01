package com.kanban.chat.models.entities;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_room")
public class ChatRoomEntity {

    private String id;
}
