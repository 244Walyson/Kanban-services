package com.kanban.chat.services.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.dtos.SuccessSaveTeamDTO;
import com.kanban.chat.dtos.TeamDTO;
import com.kanban.chat.dtos.UserDTO;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.UserRepository;
import com.kanban.chat.services.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TeamConsumer {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;
    private final String SUCCESS = "SUCCESS";

    @Transactional
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "team_mysql.kanban.tb_team_outbox"
    )
    public void consumerTeam(String message) {
        log.info("TeamConsumer: " + message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode payloadNode = jsonNode.get("payload");
            TeamDTO teamDTO = objectMapper.convertValue(payloadNode, TeamDTO.class);
            teamDTO.setImgUrl(payloadNode.get("img_url").asText());

            if(chatRoomRepository.existsById(teamDTO.getId())) {
                log.info("Team already exists in the database");
                return;
            }

            var chatRoom = new ChatRoomEntity()
                    .builder()
                    .id(teamDTO.getId())
                    .roomName(teamDTO.getName())
                    .imgUrl(teamDTO.getImgUrl())
                    .description(teamDTO.getDescription())
                    .build();

            chatRoomRepository.save(chatRoom);
            System.out.println("Payload: " + new ObjectMapper().writeValueAsString(teamDTO));

            var successSave = new SuccessSaveTeamDTO(SUCCESS, String.valueOf(teamDTO.getId()));
            kafkaProducer.sendEvent(objectMapper.writeValueAsString(successSave));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "team_mysql.kanban.tb_user_outbox"
    )
    public void consumerUser(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode payloadNode = jsonNode.get("payload");
            UserDTO userDTO = objectMapper.convertValue(payloadNode, UserDTO.class);
            userDTO.setTeamId(payloadNode.get("team_id").asLong());
            userDTO.setImgUrl(payloadNode.get("img_url").asText());

            var user = new UserEntity()
                    .builder()
                    .id(String.valueOf(userDTO.getId()))
                    .name(userDTO.getName())
                    .nickName(userDTO.getNickname())
                    .email(userDTO.getEmail())
                    .imgUrl(userDTO.getImgUrl())
                    .build();


            if(!userRepository.existsById(userDTO.getId().toString())) {
                user = userRepository.save(user);
            }
            if(chatRoomRepository.checkIfUserIsMember(userDTO.getTeamId().toString(), userDTO.getNickname())) {
                log.info("User already exists in the team");
                return;
            }
            if(!chatRoomRepository.existsById(userDTO.getTeamId().toString())) {
                log.info("Team does not exist in the database");
                return;
            }

            var chatRoom = chatRoomRepository.findById(userDTO.getTeamId().toString()).get();
            chatRoom.addMember(new UserEmbedded(user));
            chatRoomRepository.save(chatRoom);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "add-user"
    )
    public void consumerUserSaveTeam(String message) {
        try {
            var userDTO = objectMapper.readValue(message, UserDTO.class);

            log.info(userDTO.toString());

            log.info("team id " + userDTO.getTeamId());
            if(!chatRoomRepository.existsById(String.valueOf(userDTO.getTeamId()))) {
                log.info("Team does not exist in the database");
                return;
            }
            if(chatRoomRepository.checkIfUserIsMember(userDTO.getTeamId().toString(), userDTO.getNickname())) {
                log.info("User already exists in the team");
                return;
            }

            var user = new UserEntity()
                    .builder()
                    .id(String.valueOf(userDTO.getId()))
                    .name(userDTO.getName())
                    .nickName(userDTO.getNickname())
                    .email(userDTO.getEmail())
                    .imgUrl(userDTO.getImgUrl())
                    .build();


            user = userRepository.save(user);
            var chatRoom = chatRoomRepository.findById(userDTO.getTeamId().toString()).get();
            chatRoom.addMember(new UserEmbedded(user));
            chatRoomRepository.save(chatRoom);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
