package com.kanban.chat.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
  private List<ChatRoomEntity> chatRoomEntity = new ArrayList<>();

  public UserEntity() {
  }

    public UserEntity(String id, String name, String nickName, String email, String imgUrl) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.imgUrl = imgUrl;
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

  public void setChatRoomEntity(List<ChatRoomEntity> chatRoomEntity) {
    this.chatRoomEntity = chatRoomEntity;
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
}
