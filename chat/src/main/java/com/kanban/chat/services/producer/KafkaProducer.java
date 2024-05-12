package com.kanban.chat.services.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.consumer.team.save.success.topic}")
    private String successTopic;
    @Value("${spring.kafka.user.chat-created-notification.topic}")
    private String chatCreatedNotificationTopic;

    public void sendEvent(String payload) {
        try {
            log.info("Sending event topic {} with data {} ", successTopic, payload);
            kafkaTemplate.send(successTopic, payload);
        }catch (Exception e) {
            log.error("Error sending event topic {} with data {} ", successTopic, payload);
        }
    }

    public void sendChatCreatedNotification(String payload) {
        try {
            log.info("Sending event topic {} with data {} ", chatCreatedNotificationTopic, payload);
            kafkaTemplate.send(chatCreatedNotificationTopic, payload);
        }catch (Exception e) {
            log.error("Error sending event topic {} with data {} ", chatCreatedNotificationTopic, payload);
        }
    }


}
