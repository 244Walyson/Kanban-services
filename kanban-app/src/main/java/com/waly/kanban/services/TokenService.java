package com.waly.kanban.services;

import com.waly.kanban.configs.AuthorizationServerConfig;
import com.waly.kanban.dto.AccessToken;
import com.waly.kanban.entities.User;
import com.waly.kanban.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorizationServerConfig authorizationServerConfig;

    public AccessToken getToken(Authentication authentication) {
        User user;
        try {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            log.info(oidcUser.getAttributes().toString());
            user = userService.findUserByEmail(oidcUser.getEmail());
            return authorizationServerConfig.generateToken(user.getUsername(), user.getNickname(), Arrays.asList("ROLE_MEMBER"));
        } catch (Exception e) {
            OAuth2User userOauth = (OAuth2User) authentication.getPrincipal();
            log.info(userOauth.getAttributes().toString());
            user = userService.findUserByNickname(userOauth.getAttribute("login"));
            return authorizationServerConfig.generateToken(user.getUsername(), user.getNickname(), Arrays.asList("ROLE_MEMBER"));
        }

    }
}
