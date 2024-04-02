package com.kanban.chat.models.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
public class UserEntity {

  private String id;
  private String name;
  private String username;
  private String email;
  private List<ChatRoomEntity> chatRoomEntity = new ArrayList<>();

  public UserEntity() {
  }

    public UserEntity(String id, String name, String username, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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
}
