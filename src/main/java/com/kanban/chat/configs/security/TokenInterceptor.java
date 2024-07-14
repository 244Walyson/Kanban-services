package com.kanban.chat.configs.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

  private final TokenValidator tokenValidator;

  public TokenInterceptor(TokenValidator tokenValidator) {
    this.tokenValidator = tokenValidator;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestURI = request.getRequestURI();
    if (requestURI.equals("/hello-world")
            || requestURI.startsWith("/swagger-ui/index.html")
            || requestURI.startsWith("/v3/api-docs")) {
      return true;
    }
    String token = request.getHeader("Authorization");
    if (token == null) {

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
    token = token.split(" ")[1];
    String nick = tokenValidator.validateAuthentication(token);
    if (nick == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
    Authentication authentication = new UsernamePasswordAuthenticationToken(nick, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return true;
  }
}
