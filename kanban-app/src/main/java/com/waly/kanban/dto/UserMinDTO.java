package com.waly.kanban.dto;

import com.waly.kanban.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UserMinDTO {

    private Long id;
    private String name;
    private String email;
    private String nickname;
    private String imgUrl;
    private boolean connected;
    private String connectionId;

    public UserMinDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getUsername();
        this.nickname = user.getNickname();
        this.imgUrl = user.getImgUrl();
    }
}
