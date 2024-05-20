package com.waly.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.waly.notificationservice.dtos.NotificationDTO;
import com.waly.notificationservice.entities.UserNotification;
import com.waly.notificationservice.repositories.UserNotificationRepository;
import com.waly.notificationservice.utils.CustonUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private CustonUserUtil custonUserUtil;

    public void sendPushNotification(UserNotification notification) {
        String fcmToken = notification.getReceiver().getToken();
        log.info("Sending notification to: {}", fcmToken);
        if (fcmToken == null || fcmToken.isEmpty()) return;

        try {
            Notification notificationBody = Notification
                    .builder()
                    .setTitle(notification.getSender().getNickname())
                    .setBody(notification.getMessage())
                    .build();

            Message message = Message
                    .builder()
                    .setNotification(notificationBody)
                    .setToken(fcmToken)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Firebase error sending: {}", e);
        }
    }

    @Transactional
    public void sendNotifications(UserNotification notification) {
        notification = userNotificationRepository.save(notification);
        sendPushNotification(notification);
    }

    @Transactional
    public List<NotificationDTO> findMyNotifications() {
        List<UserNotification> notifications = userNotificationRepository.findByReceiverUsername(custonUserUtil.getLoggedUsername());
        return notifications.stream().filter(notification -> notification.getTitle().contains("Novo pedido de conexão") || notification.getTitle().contains("Chat criado")).map(NotificationDTO::new).toList();
    }
}