package com.waly.notificationservice.service;

import com.waly.notificationservice.dtos.UserDTO;
import com.waly.notificationservice.entities.User;
import com.waly.notificationservice.repositories.UserRepository;
import com.waly.notificationservice.utils.CustonUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private CustonUserUtil custonUserUtil;


    public UserDTO insert(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setImgUrl(dto.getImgUrl());
        user.setToken(dto.getToken());
        user = repository.save(user);
        return new UserDTO(user);
    }

    public UserDTO update(Long id, UserDTO dto) {
        User user = repository.getReferenceById(id);
        user.setName(dto.getName());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setImgUrl(dto.getImgUrl());
        user.setToken(dto.getToken());
        user = repository.save(user);
        return new UserDTO(user);
    }

}
