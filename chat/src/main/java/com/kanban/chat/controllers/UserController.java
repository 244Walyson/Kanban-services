package com.kanban.chat.controllers;

import com.kanban.chat.dtos.SaveTokenDTO;
import com.kanban.chat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/token")
    public void saveFcmToken(@RequestBody SaveTokenDTO token) {
        userService.saveFcmToken(token);
    }

}
