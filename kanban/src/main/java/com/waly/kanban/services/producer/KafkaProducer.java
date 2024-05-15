package com.waly.kanban.services.producer;


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
    @Value("${spring.kafka.add.user.topic}")
    private String orchestratorTopic;
    @Value("${spring.kafka.user.connection.topic}")
    private String userConnenectionTopic;
    @Value("${spring.kafka.user.notification.topic}")
    private String userNotificationTopic;


    public void sendEvent(String payload) {
        try {
            log.info("Sending event topic {} with data {} ", orchestratorTopic, payload);
            kafkaTemplate.send(orchestratorTopic, payload);
        }catch (Exception e) {
            log.error("Error sending event topic {} with data {} ", orchestratorTopic, payload);
        }
    }

    public void sendUserConnection(String payload) {
        try {
            log.info("Sending event topic {} with data {} ", userConnenectionTopic, payload);
            kafkaTemplate.send(userConnenectionTopic, payload);
        }catch (Exception e) {
            log.error("Error sending event topic {} with data {} ", userConnenectionTopic, payload);
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