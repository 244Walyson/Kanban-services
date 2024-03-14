package com.waly.kanban.dto;

import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Team;
import com.waly.kanban.entities.UserTeam;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamDTO {

    private Long id;
    private String name;
    private String occupationArea;
    private String description;
    private List<UserDTO> collaborators = new ArrayList<>();
    private List<BoardDTO> boards = new ArrayList<>();

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.occupationArea = team.getOccupationArea();
        this.description = team.getDescription();
        this.collaborators = team.getCollaborators().stream().map(x -> new UserDTO(x)).toList();
        this.boards = team.getBoards().stream().map(x -> new BoardDTO(x)).toList();
    }
}
