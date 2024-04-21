package com.kanban.chat.configs.security;


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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;

@Slf4j
@Component
public class TokenValidator {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private static  String BASE_JWK_SET_URL = "http://kanban:9090/.well-known/jwks.json";

    public static String validateAuthentication(String token){

        if(token == null || token.isEmpty()){
            throw new RuntimeException("Token is null or empty");
        }

        log.info(token);
        log.info(BASE_JWK_SET_URL);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();


            JWKSource<SecurityContext> keySource = JWKSourceBuilder
                    .create(new URL(BASE_JWK_SET_URL))
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
                String nickName = claimsSet.getClaim("nick").toString();

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
