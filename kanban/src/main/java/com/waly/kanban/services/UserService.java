package com.waly.kanban.services;

import com.waly.kanban.dto.UserDTO;
import com.waly.kanban.dto.UserInsertDTO;
import com.waly.kanban.dto.UserLoggedDTO;
import com.waly.kanban.dto.UserUpdateDTO;
import com.waly.kanban.entities.Role;
import com.waly.kanban.entities.User;
import com.waly.kanban.entities.UserOutbox;
import com.waly.kanban.projections.UserDetailsProjection;
import com.waly.kanban.repositories.UserOutboxRepository;
import com.waly.kanban.repositories.UserRepository;
import com.waly.kanban.util.CustonUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserOutboxRepository userOutboxRepository;
    @Autowired
    private CustonUserUtil custonUserUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

        if(result.isEmpty()) result = repository.searchUserAndRolesByNickname(username);
        if(result.isEmpty()) throw new UsernameNotFoundException("User not found");


        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    @Transactional
    protected User authenticade(){
       try{
           String username = custonUserUtil.getLoggedUsername();
           return repository.findByEmail(username).get();
       }
       catch (Exception e){
           throw new UsernameNotFoundException("User not found");
       }
    }

    @Transactional(readOnly = true)
    public UserLoggedDTO getMe(){
        User user = authenticade();
        return new UserLoggedDTO(user);
    }

    @Transactional
    public User findUserByEmail(String username) {
        return repository.findByEmail(username).get();
    }

    @Transactional
    public User findUserByNickname(String nickname) {
        return repository.findByNickname(nickname).get();
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setImgUrl(dto.getImgUrl());
        user.setBio(dto.getBio());
        user.setPassword(dto.getPassword());

        user = repository.save(user);
        userOutboxRepository.save(new UserOutbox(user));
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        User user = repository.getReferenceById(id);
        user.setName(dto.getName());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setImgUrl(dto.getImgUrl());
        user.setBio(dto.getBio());

        user = repository.save(user);
        userOutboxRepository.save(new UserOutbox(user));
        return new UserDTO(user);
    }

    public void saveOauthUser(OidcUser oidcUser) {
        if(!repository.existsByEmail(oidcUser.getEmail())){
            User user = new User();
            user.setName(oidcUser.getFullName());
            user.setImgUrl(oidcUser.getPicture());
            user.setEmail(oidcUser.getEmail());
            user.setNickname(oidcUser.getGivenName().concat("#").concat(UUID.randomUUID().toString().substring(0, 3)));
            repository.save(user);
            userOutboxRepository.save(new UserOutbox(user));
            log.info("USER ATRIBUTES " + oidcUser.getAttributes().toString());
        }
    }

}
