package com.waly.kanban.services.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waly.kanban.entities.UserOutbox;
import com.waly.kanban.repositories.UserOutboxRepository;
import com.waly.kanban.services.producer.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class SuccessSaveTeamConsumer {

    private final ObjectMapper objectMapper;
    private final UserOutboxRepository userOutboxRepository;
    private final KafkaProducer kafkaProducer;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.consumer.team.save.success.topic}"
    )
    public void consumeSuccessTeamSave(String message) {
        log.info("Consumed message: {}", message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode statusNode = jsonNode.get("status");
            JsonNode teamId = jsonNode.get("teamId");
            log.info("Payload: {}", teamId);
            if("SUCCESS".equals(statusNode.asText())) {
                log.info("Team saved successfully");
                UserOutbox userOutbox = userOutboxRepository.findByTeamId(teamId.asLong()).get();
                kafkaProducer.sendEvent(objectMapper.writeValueAsString(userOutbox));
            } else {
                log.error("Error saving team");
            }
        } catch (Exception e) {
            log.error("Error consuming message: {}", message);
        }
    }
}
