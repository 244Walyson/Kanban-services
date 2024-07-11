package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatDetailsDTO {

  private String id;
  private String name;
  private String description;
  private Integer totalCollaborators;
  private String imgUrl;
  private List<UserEmbeddedDTO> members;

  public static ChatDetailsDTO of(Chat entity) {
    return ChatDetailsDTO.builder()
            .id(entity.getId())
            .name(entity.getRoomName())
            .description(entity.getDescription())
            .totalCollaborators(entity.getMembers().size())
            .imgUrl(entity.getImgUrl())
            .build();
  }
}
