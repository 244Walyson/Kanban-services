package com.kanban.chat.dtos;

import com.kanban.chat.models.embedded.UserEmbedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserEmbeddedDTO {

  private String id;
  private String name;
  private String nickname;
  private String imgUrl;

  public static UserEmbeddedDTO of(UserEmbedded entity) {
    return UserEmbeddedDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .nickname(entity.getNickname())
            .imgUrl(entity.getImgUrl())
            .build();
  }
}
