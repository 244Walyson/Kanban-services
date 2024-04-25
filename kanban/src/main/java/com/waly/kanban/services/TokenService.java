package com.waly.kanban.services;

import com.waly.kanban.configs.AuthorizationServerConfig;
import com.waly.kanban.entities.User;
import com.waly.kanban.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

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

    public String getToken(Authentication authentication) {
        log.info("Getting token");
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        log.info(oidcUser.getAttributes().toString());
        User user = userService.findUserByEmail(oidcUser.getEmail());

        return authorizationServerConfig.generateToken(user.getUsername(), user.getNickname(), Arrays.asList("ROLE_USER"));
    }
}
