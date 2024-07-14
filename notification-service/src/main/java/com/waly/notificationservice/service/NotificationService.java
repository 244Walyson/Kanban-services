package com.waly.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.waly.notificationservice.dtos.NotificationDTO;
import com.waly.notificationservice.entities.FcmToken;
import com.waly.notificationservice.entities.UserNotification;
import com.waly.notificationservice.repositories.FcmTokenRepository;
import com.waly.notificationservice.repositories.UserNotificationRepository;
import com.waly.notificationservice.utils.CustomUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

  private final UserNotificationRepository userNotificationRepository;
  private final CustomUserUtil customUserUtil;
  private final FirebaseMessaging firebaseMessaging;
  private final FcmTokenRepository fcmTokenRepository;

  public NotificationService(UserNotificationRepository userNotificationRepository, CustomUserUtil customUserUtil, FirebaseMessaging firebaseMessaging, FcmTokenRepository fcmTokenRepository) {
    this.userNotificationRepository = userNotificationRepository;
    this.customUserUtil = customUserUtil;
    this.firebaseMessaging = firebaseMessaging;
    this.fcmTokenRepository = fcmTokenRepository;
  }

  public void sendPushNotification(NotificationDTO notification) {
    saveUserNotification(notification);
    sendNotification(notification);
  }

  private void sendNotification(NotificationDTO notification) {
    FcmToken fcmToken = fcmTokenRepository.findByUserId(notification.getReceiver().getId());
    log.info("Sending notification to: {}", fcmToken);
    if (fcmToken != null) {
      try {
        Notification notificationBody = Notification
                .builder()
                .setTitle(notification.getTitle())
                .setBody(notification.getMessage())
                .build();
        Message message = Message
                .builder()
                .setNotification(notificationBody)
                .setToken(fcmToken.getToken())
                .build();
        String response = firebaseMessaging.send(message);
        log.info("Successfully sent message: {}", response);
      } catch (FirebaseMessagingException e) {
        log.error("Firebase error sending", e);
      }
    }
  }

  private void saveUserNotification(NotificationDTO notification) {
    UserNotification userNotification = new UserNotification();
    userNotification.setTitle(notification.getTitle());
    userNotification.setMessage(notification.getMessage());
    userNotification.setSenderId(notification.getSender().getId());
    userNotification.setReceiverId(notification.getReceiver().getId());
    userNotification.setCreatedAt(notification.getCreatedAt());
    userNotificationRepository.save(userNotification);
  }

  @Transactional
  public List<NotificationDTO> findMyNotifications() {
    List<UserNotification> notifications = userNotificationRepository
            .findByReceiverId(customUserUtil.getLoggedUserId());
    return notifications.stream().map(NotificationDTO::new).toList();
  }
}