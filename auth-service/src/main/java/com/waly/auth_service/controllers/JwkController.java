package com.waly.auth_service.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.waly.auth_service.configs.AuthorizationServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class JwkController {

    private final AuthorizationServerConfig authorizationServer;

    public JwkController(AuthorizationServerConfig authorizationServer) {
        this.authorizationServer = authorizationServer;
    }

    @GetMapping(value = "/.well-known/jwks.json")
    public Map<String, Object> getJsonWebKeys() {
        RSAKey rsaKey = authorizationServer.getRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return jwkSet.toJSONObject();
    }
}
