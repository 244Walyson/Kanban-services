package com.waly.kanban.controllers;

import com.waly.kanban.configs.AuthorizationServerConfig;
import com.waly.kanban.dto.*;
import com.waly.kanban.services.TokenService;
import com.waly.kanban.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    //private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService service;
    @Autowired
    private AuthorizationServerConfig authorizationServer;
    @Autowired
    private TokenService tokenGenerator;

    @GetMapping("/me")
    public ResponseEntity<UserLoggedDTO> getMe(){
        return ResponseEntity.ok(service.getMe());
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto){
        UserDTO userDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/token")
    public ResponseEntity<AccessToken> getToken(Authentication authentication){
        return ResponseEntity.ok(tokenGenerator.getToken(authentication));
    }

    @GetMapping
    public ResponseEntity<List<UserMinDTO>> findByEmail(@RequestParam String query){
        return ResponseEntity.ok(service.findAll(query));
    }

    @PostMapping("/connect/{id}")
    public ResponseEntity<Void> connect(@PathVariable Long id) {
        return ResponseEntity.ok(service.connect(id));
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approveConnection(id));
    }
}
