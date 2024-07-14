//package com.kanban.chat.services.consumer;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kanban.chat.dtos.*;
//import com.kanban.chat.models.embedded.UserEmbedded;
//import com.kanban.chat.services.producer.KafkaProducer;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class UserConnectionConsumer {
//
//    private final ObjectMapper objectMapper;
//    private final ChatUserRepository chatUserRepository;
//    private final UserRepository userRepository;
//    private final KafkaProducer kafkaProducer;
//
//    @Transactional
//    @KafkaListener(
//            groupId = "${spring.kafka.consumer.group-id}",
//            topics = "${spring.kafka.user.connection.topic}"
//    )
//    public void consumerUserConnection(String message) {
//        log.info("Chat: " + message);
//        try {
//            JsonNode jsonNode = objectMapper.readTree(message);
//            ChatUserDTO chatUserDTO = objectMapper.convertValue(jsonNode, ChatUserDTO.class);
//            chatUserDTO.setId(chatUserDTO.getUser1().getId() + chatUserDTO.getUser2().getId());
//
//            log.info("ChatUserDTO: " + chatUserDTO.getUser2().getId() + chatUserDTO.getUser1().getId());
//            log.info("ChatUserDTO: " + chatUserDTO.getUser1().getId() + chatUserDTO.getUser2().getId());
//            log.info("ChatUserDTO: " + chatUserDTO.getId());
//
//            if(chatUserRepository.existsById(chatUserDTO.getId())) {
//                log.info("ChatUser already exists in the database");
//                return;
//            }
//
//            UserDTO userDto1 = chatUserDTO.getUser1();
//            UserDTO userDto2 = chatUserDTO.getUser2();
//            UserEntity user1 = saveUser(userDto1);
//            UserEntity user2 = saveUser(userDto2);
//
//
//
//            var chatUser = new ChatUserRoomEntity()
//                    .builder()
//                    .id("U".concat(chatUserDTO.getId()))
//                    .user1(new UserEmbedded(user1))
//                    .user2(new UserEmbedded(user2))
//                    .lastActivity(new Date())
//                    .messages(new ArrayList<>())
//                    .build();
//
//            chatUserRepository.save(chatUser);
//
//            System.out.println("Payload: " + new ObjectMapper().writeValueAsString(chatUser));
//
//            sendNotification(userDto1, userDto2);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendNotification(UserDTO sender, UserDTO receiver) throws JsonProcessingException {
//        NotificationDTO notificationDTO = new NotificationDTO();
//        notificationDTO.setSender(sender);
//        notificationDTO.setReceiver(receiver);
//        notificationDTO.setTitle("Chat created");
//        notificationDTO.setMessage("Chat criado entre " + sender.getNickname() + " e " + receiver.getNickname());
//
//        kafkaProducer.sendChatCreatedNotification(objectMapper.writeValueAsString(notificationDTO));
//    }
//    private UserEntity saveUser(UserDTO userDTO) {
//        if(userRepository.existsById(userDTO.getId().toString())) {
//            log.info("User already exists in the database");
//            return userRepository.findById(userDTO.getId().toString()).get();
//        }
//        var user = new UserEntity()
//                .builder()
//                .id(String.valueOf(userDTO.getId()))
//                .name(userDTO.getName())
//                .nickname(userDTO.getNickname())
//                .email(userDTO.getEmail())
//                .imgUrl(userDTO.getImgUrl())
//                .build();
//        return userRepository.save(user);
//    }
//
//}
