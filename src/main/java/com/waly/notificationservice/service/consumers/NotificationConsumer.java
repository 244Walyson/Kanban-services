package com.waly.notificationservice.service.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waly.notificationservice.dtos.NotificationDTO;
import com.waly.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

  private final ObjectMapper objectMapper;
  private final NotificationService notificationService;

  @Transactional
  @KafkaListener(
          groupId = "${spring.kafka.consumer.group-id}",
          topics = "${spring.kafka.user.notification.topic}"
  )
  public void consumerConnectionRequestNotification(String message) {
    log.info("Chat: {}", message);
    try {
      JsonNode jsonNode = objectMapper.readTree(message);
      NotificationDTO notification = objectMapper.convertValue(jsonNode, NotificationDTO.class);
      notificationService.sendPushNotification(notification);
    } catch (Exception e) {
      log.info("Error while processing notification ", e);
    }
  }
}
