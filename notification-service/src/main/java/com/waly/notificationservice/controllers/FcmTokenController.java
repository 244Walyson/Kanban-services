package com.waly.notificationservice.controllers;

import com.waly.notificationservice.dtos.FcmTokenDTO;
import com.waly.notificationservice.service.FcmTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm-tokens")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    public FcmTokenController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    @PostMapping
    public ResponseEntity<Void> saveFcmToken(@RequestBody FcmTokenDTO fcmTokenDTO) {
        fcmTokenService.saveFcmToken(fcmTokenDTO);
        return ResponseEntity.ok().build();
    }
}
