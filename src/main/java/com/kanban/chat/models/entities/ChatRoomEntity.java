package com.kanban.chat.models.entities;

import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "chat_room")
public class ChatRoomEntity {

    private String id;
    private String roomName;
    private String imgUrl;
    private String description;
    private List<UserEmbedded> members = new ArrayList<>();
    private List<ChatMessageEmbedded> messages = new ArrayList<>();


    public ChatRoomEntity(String id, String roomName, String imgUrl, String description) {
        this.id = id;
        this.roomName = roomName;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public void addMessage(ChatMessageEmbedded message) {
        this.messages.add(message);
    }
    public void addMember(UserEmbedded user) {
        this.members.add(user);
    }
}
