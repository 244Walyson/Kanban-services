package com.waly.auth_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waly.auth_service.dtos.*;
import com.waly.auth_service.entities.Role;
import com.waly.auth_service.entities.User;
import com.waly.auth_service.entities.UserConnection;
import com.waly.auth_service.entities.UserConnectionPK;
import com.waly.auth_service.exceptions.ResourceNotFoundException;
import com.waly.auth_service.exceptions.ValidateException;
import com.waly.auth_service.projections.UserDetailsProjection;
import com.waly.auth_service.repositories.UserConnectionRepository;
import com.waly.auth_service.repositories.UserRepository;
import com.waly.auth_service.services.producer.KafkaProducer;
import com.waly.auth_service.util.CustomUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

  private final UserRepository repository;
  private final CustomUserUtil customUserUtil;
  private final UserConnectionRepository userConnectionRepository;
  private final PasswordEncoder passwordEncoder;
  private final KafkaProducer kafkaProducer;

  public UserService(UserRepository repository, CustomUserUtil customUserUtil, UserConnectionRepository userConnectionRepository, PasswordEncoder passwordEncoder, KafkaProducer kafkaProducer) {
    this.repository = repository;
    this.customUserUtil = customUserUtil;
    this.userConnectionRepository = userConnectionRepository;
    this.passwordEncoder = passwordEncoder;
    this.kafkaProducer = kafkaProducer;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

    if (result.isEmpty()) result = repository.searchUserAndRolesByNickname(username);
    if (result.isEmpty()) throw new ResourceNotFoundException("User not found");


    User user = new User();
    user.setEmail(username);
    user.setPassword(result.get(0).getPassword());
    for (UserDetailsProjection projection : result) {
      user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
    }
    return user;
  }

  protected User authenticated() {
    String username = customUserUtil.getLoggedUsername();
    return repository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  public User findByEmail(String email) {
    return repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  public User findByNickname(String nickname) {
    return repository.findByNickname(nickname).orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Transactional
  public void saveOauthUser(OidcUser oidcUser) {
    if (!repository.existsByEmail(oidcUser.getEmail())) {
      User user = new User();
      user.setName(oidcUser.getFullName());
      user.setImgUrl(oidcUser.getPicture());
      user.setEmail(oidcUser.getEmail());
      user.setNickname(oidcUser.getGivenName().concat("#").concat(UUID.randomUUID().toString().substring(0, 3)));
      repository.save(user);
    }
  }

  @Transactional
  public UserDTO updateImage(UriDTO dto) {
    User user = authenticated();
    user.setImgUrl(dto.getUri());
    user = repository.save(user);
    return new UserDTO(user);
  }

  @Transactional
  public UserDTO insert(UserInsertDTO dto) {
    User user = new User();
    user.setName(dto.getName());
    user.setNickname(dto.getNickname());
    user.setEmail(dto.getEmail());
    user.setImgUrl(dto.getImgUrl());
    user.setBio(dto.getBio());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user = repository.save(user);
    return new UserDTO(user);
  }

  @Transactional
  public UserDTO update(Long id, UserUpdateDTO dto) {
    if (repository.existsById(id)) {
      User user = repository.getReferenceById(id);
      user.setName(dto.getName());
      user.setNickname(dto.getNickname());
      user.setEmail(dto.getEmail());
      user.setBio(dto.getBio());
      user.setImgUrl(dto.getImgUrl());
      user = repository.save(user);
      return new UserDTO(user);
    }
    throw new ResourceNotFoundException("User not found");
  }

  @Transactional(readOnly = true)
  public List<UserListDTO> findAll(String query) {
    User me = authenticated();
    List<User> users = repository.findByQuery(query);
    List<UserListDTO> dtos = new ArrayList<>();
    users.forEach(user -> {
      UserConnection userConn = userConnectionRepository.findById(new UserConnectionPK(me, user)).orElse(null);
      if (userConn == null) {
        userConn = userConnectionRepository.findById(new UserConnectionPK(user, me)).orElse(null);
      }
      UserListDTO dto = new UserListDTO(user);
      dto.setConnected(false);
      if (userConn != null) {
        dto.setConnected(userConn.isStatus());
        dto.setConnectionId("U" + userConn.getId().getUser1().getId() + userConn.getId().getUser2().getId());
      }
      dtos.add(dto);
    });
    return dtos;
  }

  @Transactional(readOnly = true)
  public UserDTO getMe() {
    User user = authenticated();
    return new UserDTO(user);
  }

  @Transactional
  public Void connect(Long id) {
    User user = authenticated();
    if (!repository.existsById(id)) throw new ResourceNotFoundException("User not found id:" +   id);
    User friend = repository.getReferenceById(id);
    if(friend.equals(user)) throw new ValidateException("Invalid Request");
    if (userConnectionRepository.existsById(new UserConnectionPK(user, friend))
            || userConnectionRepository.existsById(new UserConnectionPK(friend, user)))
      throw new ValidateException("Connection already exists");
    UserConnection userConn = new UserConnection(new UserConnectionPK(user, friend), false);
    userConnectionRepository.save(userConn);
    sendUserNotification(user, friend, "Novo pedido de conexão!", "Você recebeu um novo pedido de conexão de ");
    return null;
  }

  @Transactional
  public Void approveConnection(Long id) {
    User user = authenticated();
    if (id.equals(user.getId())) throw new ValidateException("Invalid Request, friend id: " + id);
    if (!repository.existsById(id)) throw new ResourceNotFoundException("User not found id:" + id);
    User friend = repository.getReferenceById(id);
    UserConnection userConn = userConnectionRepository.findById(new UserConnectionPK(friend, user)).orElseThrow(
            () -> new ResourceNotFoundException("Connection not found"));
    if (userConn.isStatus()) return null;
    userConn.setStatus(true);
    userConnectionRepository.save(userConn);
    sendUserNotification(user, friend, "Conexão aprovada!", "Novo chat criado com ");
    return null;
  }

  private void sendUserNotification(User user, User friend, String title, String message) {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setTitle(title);
    notificationDTO.setSender(new UserDTO(user));
    notificationDTO.setReceiver(new UserDTO(friend));
    notificationDTO.setMessage(message + user.getNickname());
    try {
      String jsonUserConn = new ObjectMapper().writeValueAsString(notificationDTO);
      kafkaProducer.sendUserNotification(jsonUserConn);
    } catch (JsonProcessingException e) {
      log.error("Error serializing Object to JSON", e);
    }
  }
}
