package com.waly.kanban.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_team_outbox")
public class TeamOutbox {

    @Id
    private Long id;
    private String name;
    private String occupationArea;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer totalCollaborators;
    private String imgUrl;

    public TeamOutbox(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.occupationArea = team.getOccupationArea();
        this.description = team.getDescription();
        this.imgUrl = team.getImgUrl();
        this.totalCollaborators = team.getTotalCollaborators();
    }

}
