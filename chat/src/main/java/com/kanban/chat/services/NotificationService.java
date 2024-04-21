package com.kanban.chat.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kanban.chat.models.entities.ChatNotificationEntity;
import com.kanban.chat.models.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private UserService userService;

    private void sendPushNotification(ChatNotificationEntity request) {
        String fcmToken = request.getFcmToken();
        if(fcmToken == null || fcmToken.isEmpty()) return;

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
        } catch (FirebaseMessagingException e) {
            log.error("Firebase error sending: {}", e);
        }
    }

    public void sendNotification(String roomId, String content, UserEntity sender) {
        List<UserEntity> users = userService.findUsersByTeam(roomId);
        users.forEach(user -> {
            if(user.getNickName().equals(sender.getNickName())) return;
            ChatNotificationEntity notification = new ChatNotificationEntity();
            notification.setFcmToken(user.getFcmToken());
            notification.setSender(sender.getNickName());
            notification.setMessage(content);
            sendPushNotification(notification);
        });
    }
}
