package com.kanban.chat.dtos;

import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FullTeamDTO {

    private String id;
    private String name;
    private String occupationArea;
    private String description;
    private Integer totalCollaborators;
    private String imgUrl;
    private List<UserDTO> members;

    public FullTeamDTO(ChatRoomEntity chatRoomEntity) {
        this.id = chatRoomEntity.getId();
        this.name = chatRoomEntity.getRoomName();
        this.description = chatRoomEntity.getDescription();
        this.totalCollaborators = chatRoomEntity.getMembers().size();
        this.imgUrl = chatRoomEntity.getImgUrl();
    }


}
