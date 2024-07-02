package com.waly.auth_service.services;

import com.waly.auth_service.entities.Role;
import com.waly.auth_service.entities.User;
import com.waly.auth_service.exceptions.ResourceNotFoundException;
import com.waly.auth_service.projections.UserDetailsProjection;
import com.waly.auth_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

        if(result.isEmpty()) result = repository.searchUserAndRolesByNickname(username);
        if(result.isEmpty()) throw new ResourceNotFoundException("User not found");


        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void saveOauthUser(OidcUser oidcUser) {
        if(!repository.existsByEmail(oidcUser.getEmail())){
            User user = new User();
            user.setName(oidcUser.getFullName());
            user.setImgUrl(oidcUser.getPicture());
            user.setEmail(oidcUser.getEmail());
            user.setNickname(oidcUser.getGivenName().concat("#").concat(UUID.randomUUID().toString().substring(0, 3)));
            repository.save(user);
            log.info("USER ATRIBUTES " + oidcUser.getAttributes().toString());
        }
    }
}
