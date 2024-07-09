package com.waly.auth_service.services;


import com.waly.auth_service.configs.AuthorizationServerConfig;
import com.waly.auth_service.dtos.AccessToken;
import com.waly.auth_service.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TokenService {

    private final UserService userService;
    private final AuthorizationServerConfig authorizationServerConfig;

    public TokenService(UserService userService, AuthorizationServerConfig authorizationServerConfig) {
        this.userService = userService;
        this.authorizationServerConfig = authorizationServerConfig;
    }

    public AccessToken getToken(Authentication authentication) {
        User user;
        try {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            log.info(oidcUser.getAttributes().toString());
            user = userService.findByEmail(oidcUser.getEmail());
            return authorizationServerConfig.generateToken(user.getUsername(), user.getNickname(), List.of("ROLE_MEMBER"));
        } catch (Exception e) {
            OAuth2User userOauth = (OAuth2User) authentication.getPrincipal();
            log.info(userOauth.getAttributes().toString());
            user = userService.findByNickname(userOauth.getAttribute("login"));
            return authorizationServerConfig.generateToken(user.getUsername(), user.getNickname(), List.of("ROLE_MEMBER"));
        }

    }
}
