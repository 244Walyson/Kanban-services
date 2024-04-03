package com.kanban.chat.configs;


import com.kanban.chat.models.JwtDTO;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;

@Slf4j
@Component
public class TokenValidator {

    public String validateAuthentication(String token){
        log.info(token);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();


            JWKSource<SecurityContext> keySource = JWKSourceBuilder
                    .create(new URL("http://localhost:8081/.well-known/jwks.json"))
                    .retrying(true)
                    .build();

            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                    expectedJWSAlg,
                    keySource);
            jwtProcessor.setJWSKeySelector(keySelector);

            SecurityContext ctx = null; // optional context parameter, not required here
            JWTClaimsSet claimsSet;
            try {
                claimsSet = jwtProcessor.process(token, ctx);

                System.out.println(claimsSet.toJSONObject());
                String nickName = claimsSet.getClaim("nickname").toString();

                return nickName;
            } catch (ParseException | BadJOSEException e) {
                // Invalid token
                System.err.println(e.getMessage());
                return null;
            } catch (JOSEException e) {
                System.err.println(e.getMessage());
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error while trying to process the Access Token");
        }
    }
}
