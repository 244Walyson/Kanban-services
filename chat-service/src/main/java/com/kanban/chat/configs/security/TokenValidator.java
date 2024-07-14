package com.kanban.chat.configs.security;


import com.kanban.chat.exceptions.ForbiddenException;
import com.kanban.chat.exceptions.UnauthorizedException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class TokenValidator {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String baseJwkSetUrl;

    public TokenValidator() {
    }

    public TokenValidator(String baseJwkSetUrl) {
        this.baseJwkSetUrl = baseJwkSetUrl;
    }

    public String validateAuthentication(String token){
        if(token == null || token.isEmpty()){
            throw new UnauthorizedException("Invalid credentials");
        }
        try {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            long ttl = (long) 60 * 60 * 1000; // 1 hour
            long refreshTimeout = (long) 60 * 1000;
            JWKSource<SecurityContext> keySource = JWKSourceBuilder
                    .create(new URI(baseJwkSetUrl).toURL())
                    .retrying(true)
                    .cache(true)
                    .cache(ttl, refreshTimeout)
                    .build();
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                    expectedJWSAlg,
                    keySource);
            jwtProcessor.setJWSKeySelector(keySelector);
            JWTClaimsSet claimsSet;
            claimsSet = jwtProcessor.process(token, null);
            return claimsSet.getClaim("nick").toString();
        }catch (Exception e){
            log.error("Error while trying to process the Access Token", e);
            throw new ForbiddenException("Error while trying to process the Access Token");
        }
    }
}
