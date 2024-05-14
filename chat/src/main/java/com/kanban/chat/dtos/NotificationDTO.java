package com.kanban.chat.dtos;

import java.util.Date;

public class NotificationDTO {

    private String id;
    private UserDTO sender;
    private UserDTO receiver;
    private String title;
    private String message;
    private Date createdAt;

    public NotificationDTO() {
        this.createdAt = new Date();
    }

    public NotificationDTO(String id, UserDTO sender, UserDTO receiver, String title, String message) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.message = message;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
