package com.waly.notificationservice.dtos;

import com.waly.notificationservice.entities.UserNotification;
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
    private UserDTO sender;
    private UserDTO receiver;
    private String message;
    private Date createdAt;

    public NotificationDTO(UserNotification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.createdAt = notification.getCreatedAt();
    }
}
