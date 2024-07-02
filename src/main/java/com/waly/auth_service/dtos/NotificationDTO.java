package com.waly.auth_service.dtos;

import lombok.ToString;

import java.util.Date;

@ToString
public class NotificationDTO {

    private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private String message;
    private Date createdAt;

    public NotificationDTO() {
        this.createdAt = new Date();
    }

    public NotificationDTO(Long id, UserDTO sender, UserDTO receiver, String message) {
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
