package com.kanban.chat.models.entities;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_notification")
public class ChatNotificationEntity {

  private String id;
  private UserEntity sender;
  private UserEntity receiver;
  private String message;

  public ChatNotificationEntity() {
  }

  public ChatNotificationEntity(String id, UserEntity sender, UserEntity receiver, String message) {
    this.id = id;
    this.sender = sender;
    this.receiver = receiver;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserEntity getSender() {
    return sender;
  }

  public void setSender(UserEntity sender) {
    this.sender = sender;
  }

  public UserEntity getReceiver() {
    return receiver;
  }

  public void setReceiver(UserEntity receiver) {
    this.receiver = receiver;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
