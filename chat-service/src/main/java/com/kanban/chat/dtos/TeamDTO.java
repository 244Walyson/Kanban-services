package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamDTO {

    private String id;
    private String name;
    private String occupationArea;
    private String description;
    private Integer totalCollaborators;
    private String imgUrl;

    public TeamDTO(ChatRoomEntity chatRoomEntity) {
        this.id = chatRoomEntity.getId();
        this.name = chatRoomEntity.getRoomName();
        this.description = chatRoomEntity.getDescription();
        this.totalCollaborators = chatRoomEntity.getMembers().size();
        this.imgUrl = chatRoomEntity.getImgUrl();
    }
}
