package com.waly.notificationservice.entities;

public class User {

    private Long id;
    private String name;
    private String nickName;
    private String email;
    private String imgUrl;
    private String token;

    public User() {
    }

    public User(Long id, String name, String nickName, String email, String imgUrl, String token) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.imgUrl = imgUrl;
        this.token = token;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
