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
    private UserEmbedded receiver;
    private String content;
    private Instant timestamp;
    private MessageStatus status;

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(String id, UserEmbedded sender, UserEmbedded receiver, String content, Instant timestamp, MessageStatus status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
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

    public UserEmbedded getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEmbedded receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
