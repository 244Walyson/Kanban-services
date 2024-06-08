package com.kanban.chat.controllers;

import com.kanban.chat.dtos.SaveTokenDTO;
import com.kanban.chat.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/token")
    public void saveFcmToken(@RequestBody SaveTokenDTO token) {
        log.info("Token received: " + token.getToken());
        userService.saveFcmToken(token);
    }

}
