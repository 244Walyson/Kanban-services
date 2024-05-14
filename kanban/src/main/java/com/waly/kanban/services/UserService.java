package com.waly.kanban.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waly.kanban.dto.*;
import com.waly.kanban.entities.*;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.projections.UserDetailsProjection;
import com.waly.kanban.repositories.UserConnectionRepository;
import com.waly.kanban.repositories.UserOutboxRepository;
import com.waly.kanban.repositories.UserRepository;
import com.waly.kanban.services.producer.KafkaProducer;
import com.waly.kanban.util.CustonUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private UserConnectionRepository userConnectionRepository;
    @Autowired
    private KafkaProducer kafkaProducer;

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

    @Transactional
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

    @Transactional(readOnly = true)
    public List<UserMinDTO> findAll(String query) {
        User me = authenticade();
        List<User> users = repository.findByQuery(query);
        List<UserMinDTO> dtos = new ArrayList<>();

        users.forEach(user -> {
            UserConnection userConn = userConnectionRepository.findById(new UserConnectionPK(me, user)).orElse(null);
            if(userConn == null){
                userConn = userConnectionRepository.findById(new UserConnectionPK(user, me)).orElse(null);
            }
            UserMinDTO dto = new UserMinDTO(user);
            dto.setConnected(false);
            if(userConn != null){
                dto.setConnected(userConn.isStatus());
                dto.setConnectionId("U" + userConn.getId().getUser1().getId() + userConn.getId().getUser2().getId());
            }
            dtos.add(dto);
        });

        return dtos;
    }

    @Transactional
    public Void connect(Long id) {
        User user = authenticade();
        User friend = repository.getReferenceById(id);
        if(friend == null) throw new NotFoundException("User not found id:" + id);
        UserConnection userConn = new UserConnection(new UserConnectionPK(user, friend), false);
        userConnectionRepository.save(userConn);

        ConnectionNotificationDTO connectionNotificationDTO = new ConnectionNotificationDTO(null, new UserMinDTO(user), new UserMinDTO(friend), "New connection request");

        try {
            String jsonConnectionNotification = new ObjectMapper().writeValueAsString(connectionNotificationDTO);

            kafkaProducer.sendUserNotification(jsonConnectionNotification);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar ConnectionNotificationDTO para JSON", e);
        }
        return null;
    }

    @Transactional
    public Void approveConnection(Long id) {
        User user = authenticade();
        User friend = repository.getReferenceById(id);
        if(friend == null) throw new NotFoundException("User not found id:" + id);
        UserConnection userConn = userConnectionRepository.findById(new UserConnectionPK(friend, user)).orElseThrow(() -> {
            throw new NotFoundException("Connection not found");
        });
        userConn.setStatus(true);
        userConn = userConnectionRepository.save(userConn);
        UserConnectionDTO userConnDTO = new UserConnectionDTO(userConn);

        try {
            String jsonUserConn = new ObjectMapper().writeValueAsString(userConnDTO);

            kafkaProducer.sendUserConnection(jsonUserConn);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar UserConnectionDTO para JSON", e);
        }

        log.info("UserConnectionDTO: " + userConnDTO.toString());

        return null;
    }

}
