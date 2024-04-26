package com.kanban.chat.models.entities;

import com.kanban.chat.dtos.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Document(collection = "user")
public class UserEntity {

  private String id;
  private String name;
  private String nickName;
  private String imgUrl;
  private String email;
  private String fcmToken;
  @DBRef
  private List<ChatRoomEntity> chatRoomEntity = new ArrayList<>();

  public UserEntity() {
  }

    public UserEntity(String id, String name, String nickName, String email, String imgUrl, String fcmToken) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.imgUrl = imgUrl;
        this.fcmToken = fcmToken;
    }

    public UserEntity(UserDTO userDTO) {
        this.id = userDTO.getId().toString();
        this.name = userDTO.getName();
        this.nickName = userDTO.getNickname();
        this.email = userDTO.getEmail();
        this.imgUrl = userDTO.getImgUrl();
    }

    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void addChatRoomEntity(ChatRoomEntity chatRoomEntity) {
    this.chatRoomEntity.add(chatRoomEntity);
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getFcmToken() {
    return fcmToken;
  }

  public void setFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }
}
