package com.waly.kanban.dto;

import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDTO {

    private Long id;
    private String title;
    private String description;
    private Integer position;

    public CardDTO(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.position = card.getCardPosition();
        this.description = card.getDescription();
    }
}
