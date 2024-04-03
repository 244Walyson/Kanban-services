package com.kanban.chat.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class CustomUserUtil {

    public String getLoggedUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info(principal.toString());
        if (principal instanceof String) {
            return (String) principal;
        } else {
            return null;
        }
    }
    public String getLoggedNickname(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        return jwtPrincipal.getClaim("nickname");
    }
}

