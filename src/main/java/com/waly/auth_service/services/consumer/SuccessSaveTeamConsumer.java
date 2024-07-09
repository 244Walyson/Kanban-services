package com.waly.auth_service.services.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waly.auth_service.services.producer.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class SuccessSaveTeamConsumer {

    private final ObjectMapper objectMapper;

//    @KafkaListener(
//            groupId = "${spring.kafka.consumer.group-id}",
//            topics = "${spring.kafka.consumer.team.save.success.topic}"
//    )
    public void consumeSuccessTeamSave(String message) {
        log.info("Consumed message: {}", message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode statusNode = jsonNode.get("status");
            JsonNode teamId = jsonNode.get("teamId");
            log.info("Payload: {}", teamId);
            if("SUCCESS".equals(statusNode.asText())) {
                log.info("Team saved successfully");
            } else {
                log.error("Error saving team");
            }
        } catch (Exception e) {
            log.error("Error consuming message: {}", message);
        }
    }
}
