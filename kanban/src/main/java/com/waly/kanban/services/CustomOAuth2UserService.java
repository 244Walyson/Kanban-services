package com.waly.kanban.services;

import com.waly.kanban.dto.CustomOAuth2User;
import com.waly.kanban.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        log.info("USER ATRIBUTES " + user.getAttributes().toString());
        return new CustomOAuth2User(user);
    }

    public void save(OidcUser oidcUser) {
        User user = new User();
        user.setName(oidcUser.getName());
        user.setImgUrl(oidcUser.getPicture());
        user.setEmail(oidcUser.getEmail());
        log.info("USER ATRIBUTES " + oidcUser.getAttributes().toString());
    }
}