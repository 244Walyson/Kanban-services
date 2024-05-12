package com.waly.kanban.entities;

import com.waly.kanban.dto.UserMinDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_connection_notification")
public class ConnectionNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private User sender;
    private User receiver;
    private String message;

    public ConnectionNotification() {
    }

    public ConnectionNotification(User sender, User receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
