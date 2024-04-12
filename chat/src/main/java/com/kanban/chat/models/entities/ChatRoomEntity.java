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
    private String roomName;
    private String imgUrl;
    private String description;
    private List<UserEmbedded> members = new ArrayList<>();
    private List<ChatMessageEmbedded> messages = new ArrayList<>();

    public ChatRoomEntity() {
    }

    public ChatRoomEntity(String id, String roomName, String imgUrl, String description) {
        this.id = id;
        this.roomName = roomName;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<UserEmbedded> getMembers() {
        return members;
    }

    public void addMember(UserEmbedded user) {
        this.members.add(user);
    }

    public List<ChatMessageEmbedded> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessageEmbedded message) {
        this.messages.add(message);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
