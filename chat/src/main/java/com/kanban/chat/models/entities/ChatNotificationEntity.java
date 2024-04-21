package com.kanban.chat.models.entities;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_notification")
public class ChatNotificationEntity {

  private String id;
  private String sender;
  private String message;
  private String fcmToken;

  public ChatNotificationEntity() {
  }

  public ChatNotificationEntity(String id, String sender, String message, String fcmToken) {
    this.id = id;
    this.sender = sender;
    this.message = message;
    this.fcmToken = fcmToken;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getFcmToken() {
    return fcmToken;
  }

  public void setFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }
}
