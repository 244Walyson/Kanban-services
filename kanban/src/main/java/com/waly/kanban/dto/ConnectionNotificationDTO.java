package com.waly.kanban.dto;

import lombok.ToString;

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
