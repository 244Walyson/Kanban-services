package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String imgUrl;
    private Long teamId;

    public UserDTO(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.name = userEntity.getName();
        this.nickname = userEntity.getNickName();
        this.email = userEntity.getEmail();
        this.imgUrl = userEntity.getImgUrl();
    }


}
