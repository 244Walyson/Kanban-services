package com.kanban.chat.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamDTO {

    private Long id;
    private String name;
    private String occupationArea;
    private String description;
    private Integer totalCollaborators;
    private String imgUrl;
}
