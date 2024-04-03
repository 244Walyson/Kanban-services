package com.kanban.chat.models.entities;

import java.time.Instant;

import com.kanban.chat.models.embedded.UserEmbedded;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_message")
public class ChatMessageEntity {

    @Id
    private String id;
    private UserEmbedded sender;
    private String content;
    private Instant instant;
    private MessageStatus status;

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(String id, UserEmbedded sender, String content, Instant instant, MessageStatus status) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.instant = instant;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEmbedded getSender() {
        return sender;
    }

    public void setSender(UserEmbedded sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}
