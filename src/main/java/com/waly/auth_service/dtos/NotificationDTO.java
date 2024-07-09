package com.waly.auth_service.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
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
}
