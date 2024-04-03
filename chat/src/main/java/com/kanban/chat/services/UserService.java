package com.kanban.chat.services;

import com.kanban.chat.models.entities.UserEntity;
import com.kanban.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;


    @Transactional
    public UserEntity getLoggedUser(String nickname){
        return repository.findByNickName(nickname);
    }

}
