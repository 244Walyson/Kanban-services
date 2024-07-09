package com.waly.auth_service.services.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    @Value("${spring.kafka.user.connection.topic}")
    private String userConnectionTopic;
    @Value("${spring.kafka.user.notification.topic}")
    private String userNotificationTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserConnection(String payload) {
        try {
            log.info("Sending event topic {} with data {} ", userConnectionTopic, payload);
            kafkaTemplate.send(userConnectionTopic, payload);
        }catch (Exception e) {
            log.error("Error sending event topic {} with data {} ", userConnectionTopic, payload);
        }
    }

    public void sendUserNotification(String payload) {
        try {
            log.info("Sending event topic {} with data {} ", userNotificationTopic, payload);
            kafkaTemplate.send(userNotificationTopic, payload);
        }catch (Exception e) {
            log.error("Error sending event topic {} with data {} ", userNotificationTopic, payload);
        }
    }



}