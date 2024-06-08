package com.waly.notificationservice.entities;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "tb_notification")
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    private String message;
    private Date createdAt;
    private Boolean accepted = false;
    private Status status;

    public UserNotification() {
    }

    public UserNotification(Long id, String title, User sender, User receiver, String message, Boolean accepted, Date createdAt, Status status) {
        this.id = id;
        this.title = title;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
        this.accepted = accepted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
