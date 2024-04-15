package com.waly.kanban.dto;

import com.waly.kanban.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoggedDTO {

    private Long id;
    private String name;
    private String username;
    private String nickname;
    private String email;
    private String imgUrl;
    private String bio;

    public UserLoggedDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imgUrl = user.getImgUrl();
        this.bio = user.getBio();
        this.nickname = user.getNickname();
    }
}
