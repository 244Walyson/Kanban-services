package com.kanban.chat.services;

import com.kanban.chat.dtos.SaveTokenDTO;
import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.UserRepository;
import com.kanban.chat.utils.CustomUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private CustomUserUtil customUserUtil;


    @Transactional
    public UserEntity getLoggedUser(String nickname){
        return repository.findByNickName(nickname);
    }


    @Transactional
    public void saveFcmToken(SaveTokenDTO token) {
        UserEntity user = getLoggedUser(customUserUtil.getLoggedUsername());
        user.setFcmToken(token.getToken());
        repository.save(user);
    }

    @Transactional
    public List<UserEntity> findUsersByTeam(String teamId){
        return repository.findByChatRoomEntityId(teamId);
    }
}
