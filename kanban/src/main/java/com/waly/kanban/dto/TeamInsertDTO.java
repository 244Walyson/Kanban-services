package com.waly.kanban.dto;

import com.waly.kanban.entities.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamInsertDTO {

    private String name;
    private String occupationArea;
    private String description;
    private String imgUrl;
    private String githubLink;

}
