package com.kanban.chat.models.embedded;

import com.kanban.chat.models.entities.UserEntity;

import java.time.Instant;

public class UserEmbedded {

    private String id;
    private String name;
    private String username;

    public UserEmbedded() {
    }

    public UserEmbedded(String id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public UserEmbedded(UserEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.username = entity.getUsername();
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

}
