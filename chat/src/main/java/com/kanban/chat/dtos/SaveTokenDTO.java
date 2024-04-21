package com.kanban.chat.dtos;

public class SaveTokenDTO {

    private String token;

    public SaveTokenDTO() {
    }

    public SaveTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
