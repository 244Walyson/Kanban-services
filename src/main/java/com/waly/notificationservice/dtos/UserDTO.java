package com.waly.notificationservice.dtos;


import com.waly.notificationservice.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String imgUrl;
    private String token;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.imgUrl = user.getImgUrl();
        this.token = user.getToken();
    }
}
