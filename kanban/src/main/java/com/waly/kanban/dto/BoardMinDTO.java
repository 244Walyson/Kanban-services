package com.waly.kanban.dto;

import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardMinDTO {

    private Long id;
    private String title;
    private Integer totalCards;
    public BoardMinDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.totalCards = board.getTotalCards();
    }
}
