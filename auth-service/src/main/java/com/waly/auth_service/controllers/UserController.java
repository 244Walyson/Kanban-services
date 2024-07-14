package com.waly.auth_service.controllers;


import com.waly.auth_service.dtos.*;
import com.waly.auth_service.services.TokenService;
import com.waly.auth_service.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    private final UserService service;
    private final TokenService tokenGenerator;

    public UserController(UserService service, TokenService tokenGenerator) {
        this.service = service;
        this.tokenGenerator = tokenGenerator;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(){
        return ResponseEntity.ok(service.getMe());
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto){
        UserDTO userDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PutMapping("/update-image")
    public ResponseEntity<UserDTO> updateImage(@Valid @RequestBody UriDTO dto){
        return ResponseEntity.ok(service.updateImage(dto));
    }

    @GetMapping("/token")
    public ResponseEntity<AccessToken> getToken(Authentication authentication){
        return ResponseEntity.ok(tokenGenerator.getToken(authentication));
    }

    @GetMapping
    public ResponseEntity<List<UserListDTO>> findAll(@RequestParam(defaultValue = "") String query){
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
