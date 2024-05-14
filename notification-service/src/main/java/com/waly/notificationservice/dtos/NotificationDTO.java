package com.waly.notificationservice.dtos;

import com.waly.notificationservice.entities.Status;
import com.waly.notificationservice.entities.User;
import com.waly.notificationservice.entities.UserNotification;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDTO {

    private Long id;
    private String title;
    private User sender;
    private String message;
    private Date createdAt;
    private Status status;

    public NotificationDTO(UserNotification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.sender = notification.getSender();
        this.message = notification.getMessage();
        this.createdAt = notification.getCreatedAt();
        this.status = notification.getStatus();
    }
}
