package com.waly.notificationservice.controllers;

import com.waly.notificationservice.dtos.FcmTokenDTO;
import com.waly.notificationservice.dtos.UserDTO;
import com.waly.notificationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserDTO dto) {
        UserDTO userDTO = userService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok().body(userService.update(id, dto));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> saveFcmToken(@RequestBody FcmTokenDTO token) {
        return ResponseEntity.ok().body(userService.saveFcmToken(token));
    }
}
