//package com.kanban.chat.services.consumer;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kanban.chat.models.embedded.UserEmbedded;
//import com.kanban.chat.models.entities.ChatRoomEntity;
//import com.kanban.chat.repositories.ChatRoomRepository;
//import com.kanban.chat.services.producer.KafkaProducer;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class TeamConsumer {
//
//    private final ObjectMapper objectMapper;
//    private final KafkaProducer kafkaProducer;
//    private final String SUCCESS = "SUCCESS";
//
//    @Transactional
//    @KafkaListener(
//            groupId = "${spring.kafka.consumer.group-id}",
//            topics = "team_mysql.kanban.tb_team_outbox"
//    )
//    public void consumerTeam(String message) {
//        log.info("TeamConsumer: " + message);
//        try {
//            JsonNode jsonNode = objectMapper.readTree(message);
//            JsonNode payloadNode = jsonNode.get("payload");
//            TeamDTO teamDTO = objectMapper.convertValue(payloadNode, TeamDTO.class);
//            teamDTO.setImgUrl(payloadNode.get("img_url").asText());
//
//            if(chatRoomRepository.existsById("R".concat(teamDTO.getId()))) {
//                log.info("Team already exists in the database");
//                var chatRoom = chatRoomRepository.findById("R".concat(teamDTO.getId())).get();
//                chatRoom.setRoomName(teamDTO.getName());
//                chatRoom.setImgUrl(teamDTO.getImgUrl());
//                chatRoom.setDescription(teamDTO.getDescription());
//                chatRoomRepository.save(chatRoom);
//                return;
//            }
//
//            var chatRoom = ChatRoomEntity
//                    .builder()
//                    .id("R".concat(teamDTO.getId()))
//                    .roomName(teamDTO.getName())
//                    .imgUrl(teamDTO.getImgUrl())
//                    .description(teamDTO.getDescription())
//                    .build();
//
//            chatRoomRepository.save(chatRoom);
//            System.out.println("Payload: " + new ObjectMapper().writeValueAsString(teamDTO));
//
//            var successSave = new SuccessSaveTeamDTO(SUCCESS, String.valueOf(teamDTO.getId()));
//            kafkaProducer.sendEvent(objectMapper.writeValueAsString(successSave));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional
//    @KafkaListener(
//            groupId = "${spring.kafka.consumer.group-id}",
//            topics = "team_mysql.kanban.tb_user_outbox"
//    )
//    public void consumerUser(String message) {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(message);
//            JsonNode payloadNode = jsonNode.get("payload");
//            UserDTO userDTO = objectMapper.convertValue(payloadNode, UserDTO.class);
//            userDTO.setTeamId(payloadNode.get("team_id").asLong());
//            userDTO.setImgUrl(payloadNode.get("img_url").asText());
//
//            var user = new UserEntity()
//                    .builder()
//                    .id(String.valueOf(userDTO.getId()))
//                    .name(userDTO.getName())
//                    .nickname(userDTO.getNickname())
//                    .email(userDTO.getEmail())
//                    .imgUrl(userDTO.getImgUrl())
//                    .build();
//
//
//            user = userRepository.save(user);
//
//            if(chatRoomRepository.checkIfUserIsMember("R".concat(userDTO.getTeamId().toString()), userDTO.getNickname())) {
//                log.info("User already exists in the team");
//                return;
//            }
//            if(!chatRoomRepository.existsById("R".concat(userDTO.getTeamId().toString()))) {
//                log.info("Team does not exist in the database");
//                return;
//            }
//
//            var chatRoom = chatRoomRepository.findById("R".concat(userDTO.getTeamId().toString())).get();
//            chatRoom.addMember(new UserEmbedded(user));
//            chatRoomRepository.save(chatRoom);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Transactional
//    @KafkaListener(
//            groupId = "${spring.kafka.consumer.group-id}",
//            topics = "add-user"
//    )
//    public void consumerUserSaveTeam(String message) {
//        try {
//            var userDTO = objectMapper.readValue(message, UserDTO.class);
//
//            log.info(userDTO.toString());
//
//            log.info("team id " + userDTO.getTeamId());
//            if(!chatRoomRepository.existsById("R".concat(String.valueOf(userDTO.getTeamId())))) {
//                log.info("Team does not exist in the database");
//                return;
//            }
//            if(chatRoomRepository.checkIfUserIsMember("R".concat(userDTO.getTeamId().toString()), userDTO.getNickname())) {
//                log.info("User already exists in the team");
//                return;
//            }
//
//            var user = new UserEntity()
//                    .builder()
//                    .id(String.valueOf(userDTO.getId()))
//                    .name(userDTO.getName())
//                    .nickname(userDTO.getNickname())
//                    .email(userDTO.getEmail())
//                    .imgUrl(userDTO.getImgUrl())
//                    .build();
//
//
//            user = userRepository.save(user);
//            var chatRoom = chatRoomRepository.findById("R".concat(userDTO.getTeamId().toString())).get();
//            chatRoom.addMember(new UserEmbedded(user));
//            chatRoomRepository.save(chatRoom);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
