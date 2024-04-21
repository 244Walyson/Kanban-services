package com.kanban.chat.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kanban.chat.models.entities.ChatNotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    public String sendPushNotification(ChatNotificationEntity request) {
        try {
            Notification notification = Notification
                    .builder()
                    .setTitle(request.getSender())
                    .setBody(request.getMessage())
                    .build();

            Message message = Message
                    .builder()
                    .setNotification(notification)
                    .setToken(request.getFcmToken())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Firebase error sending: {}", e);

            return "Firebase error sending";
        }
    }
}
