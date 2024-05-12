package com.waly.kanban.dto;

import com.waly.kanban.entities.ConnectionNotification;
import com.waly.kanban.entities.User;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;

@ToString
public class ConnectionNotificationDTO {

    private Long id;
    private UserMinDTO sender;
    private UserMinDTO receiver;
    private String message;
    private Date createdAt;

    public ConnectionNotificationDTO() {
        this.createdAt = new Date();
    }

    public ConnectionNotificationDTO(Long id, UserMinDTO sender, UserMinDTO receiver, String message) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.createdAt = new Date();
    }

    public ConnectionNotificationDTO(ConnectionNotification connectionNotification) {
        this.id = connectionNotification.getId();
        this.sender = new UserMinDTO(connectionNotification.getSender());
        this.receiver = new UserMinDTO(connectionNotification.getReceiver());
        this.message = connectionNotification.getMessage();
        this.createdAt = connectionNotification.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserMinDTO getSender() {
        return sender;
    }

    public void setSender(UserMinDTO sender) {
        this.sender = sender;
    }

    public UserMinDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserMinDTO receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

}
