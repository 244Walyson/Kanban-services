package com.kanban.chat.models.entities;

import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chat_room")
public class ChatRoomEntity {

    private String id;
    private String name;
    private List<UserEmbedded> users = new ArrayList<>();
    private List<ChatMessageEmbedded> messages = new ArrayList<>();

    public ChatRoomEntity() {
    }

    public ChatRoomEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserEmbedded> getUsers() {
        return users;
    }

    public void addUser(UserEmbedded user) {
        this.users.add(user);
    }

    public List<ChatMessageEmbedded> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessageEmbedded message) {
        this.messages.add(message);
    }
}
