package com.waly.kanban.dto;

import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import com.waly.kanban.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDTO {

    private Long id;
    private String title;
    private String description;
    private Integer position;
    private boolean done;
    private Integer priority;
    private List<UserMinDTO> collaborators = new ArrayList<>();

    public CardDTO(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.position = card.getCardPosition();
        this.description = card.getDescription();
        this.done = card.isDone();
        this.collaborators = card.getCollaborators().stream().map(x -> new UserMinDTO(x)).toList();
    }
}
