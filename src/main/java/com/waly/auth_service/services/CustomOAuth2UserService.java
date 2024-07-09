package com.waly.auth_service.services;

import com.waly.auth_service.dtos.CustomOAuth2User;
import com.waly.auth_service.entities.User;
import com.waly.auth_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        if(!userRepository.existsByNickname(user.getAttribute("login"))){
            User userEntity = new User();
            userEntity.setEmail(user.getAttribute("email"));
            userEntity.setName(user.getAttribute("name"));
            userEntity.setImgUrl(user.getAttribute("avatar_url"));
            String login = user.getAttribute("login");
            userEntity.setNickname(login);
            if(userEntity.getUsername() == null) {
                userEntity.setEmail(login.toString().concat("@github.com"));
            }
            userRepository.save(userEntity);
        }
        return new CustomOAuth2User(user);
    }

    public void save(OidcUser oidcUser) {
        User user = new User();
        user.setName(oidcUser.getName());
        user.setImgUrl(oidcUser.getPicture());
        user.setEmail(oidcUser.getEmail());
    }
}