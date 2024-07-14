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
public class ChatDTO {

    private String id;
    private String roomName;
    private String imgUrl;
    private Integer totalMembers;
    private String description;
    private List<MessageDTO> messages;

    public static ChatDTO of(Chat entity) {
        return ChatDTO.builder()
                .id(entity.getId())
                .roomName(entity.getRoomName())
                .imgUrl(entity.getImgUrl())
                .totalMembers(entity.getMembers().size())
                .description(entity.getDescription())
                .build();
    }
}
