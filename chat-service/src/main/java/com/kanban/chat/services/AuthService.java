package com.kanban.chat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.repositories.ChatRoomRepository;
import com.kanban.chat.repositories.ChatUserRepository;
import com.kanban.chat.utils.CustomUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("authService")
public class AuthService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private CustomUserUtil customUserUtil;


    public boolean isMemberOfChat(String nickName, String chatRoomId) {
        log.info("Checking if user is member of chat " + nickName + " " + chatRoomId);
        boolean isMember = chatRoomRepository.checkIfUserIsMember(chatRoomId, nickName);
        log.info(String.valueOf(isMember));
        if(!isMember) isMember = chatUserRepository.checkIfUserIsMember(chatRoomId, nickName);
        return isMember;
    }
}
