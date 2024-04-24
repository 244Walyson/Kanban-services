package com.waly.kanban.controllers;

import com.waly.kanban.dto.UserDTO;
import com.waly.kanban.dto.UserInsertDTO;
import com.waly.kanban.dto.UserLoggedDTO;
import com.waly.kanban.dto.UserUpdateDTO;
import com.waly.kanban.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;


    @GetMapping("/me")
    public ResponseEntity<UserLoggedDTO> getMe(){
        return ResponseEntity.ok(service.getMe());
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto){
        return ResponseEntity.ok(service.insert(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }
}
