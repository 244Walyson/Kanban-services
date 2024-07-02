package com.waly.auth_service.controllers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.waly.auth_service.configs.AuthorizationServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class JwkController {

    @Autowired
    private AuthorizationServerConfig authorizationServer;

    @GetMapping(value = "/.well-known/jwks.json")
    public Map<String, Object> getJwks() throws JOSEException {
        RSAKey rsaKey = authorizationServer.getRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return jwkSet.toJSONObject();
    }
}
