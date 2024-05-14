package com.kanban.chat.models.embedded;

import com.kanban.chat.models.entities.UserEntity;

import java.time.Instant;

public class UserEmbedded {

    private String id;
    private String name;
    private String nickname;
    private String imgUrl;

    public UserEmbedded() {
    }

    public UserEmbedded(String id, String name, String nickname, String imgUrl) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.imgUrl = imgUrl;
    }

    public UserEmbedded(UserEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.nickname = entity.getNickname();
        this.imgUrl = entity.getImgUrl();
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}

