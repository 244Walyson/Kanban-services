package com.kanban.chat.models.embedded;

import com.kanban.chat.models.entities.UserEntity;

import java.time.Instant;

public class UserEmbedded {

    private String id;
    private String name;
    private String nickName;
    private String fcmToken;

    public UserEmbedded() {
    }

    public UserEmbedded(String id, String name, String nickName, String fcmToken) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.fcmToken = fcmToken;
    }

    public UserEmbedded(UserEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.nickName = entity.getNickName();
        this.fcmToken = entity.getFcmToken();
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

    public void setnickName(String username) {
        this.nickName = username;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
