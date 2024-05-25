package com.waly.kanban.dto;

import com.waly.kanban.entities.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardMinDTO {

    private Long id;
    private String title;
    private String description;
    private Integer position;
    private boolean done;

    public CardMinDTO(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.position = card.getCardPosition();
        this.description = card.getDescription();
        this.done = card.isDone();
    }
}
