package com.kanban.chat.models.entities;

import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "chat_user_room")
public class ChatUserRoomEntity {

    private String id;
    private UserEmbedded user1;
    private UserEmbedded user2;
    private List<ChatMessageEmbedded> messages = new ArrayList<>();


    public ChatUserRoomEntity(String id, UserEmbedded user1, UserEmbedded user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }


    public void addMessage(ChatMessageEmbedded message) {
        this.messages.add(message);
    }

    public List<ChatMessageEmbedded> getMessages() {
        return messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEmbedded getUser1() {
        return user1;
    }

    public void setUser1(UserEmbedded user1) {
        this.user1 = user1;
    }


    public UserEmbedded getUser2() {
        return user2;
    }

    public void setUser2(UserEmbedded user2) {
        this.user2 = user2;
    }
}
