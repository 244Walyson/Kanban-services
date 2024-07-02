package com.waly.auth_service.dtos;

import com.waly.auth_service.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UserListDTO {

    private Long id;
    private String name;
    private String email;
    private String nickname;
    private String imgUrl;
    private boolean connected;
    private String connectionId;

    public UserListDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getUsername();
        this.nickname = user.getNickname();
        this.imgUrl = user.getImgUrl();
    }
}
