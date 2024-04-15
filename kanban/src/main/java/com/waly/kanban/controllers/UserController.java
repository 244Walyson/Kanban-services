package com.waly.kanban.controllers;

import com.waly.kanban.dto.UserDTO;
import com.waly.kanban.dto.UserLoggedDTO;
import com.waly.kanban.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    
    @GetMapping("/me")
    public ResponseEntity<UserLoggedDTO> getMe(){
        return ResponseEntity.ok(service.getMe());
    }
}
