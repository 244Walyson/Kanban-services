package com.waly.kanban.dto;

import com.waly.kanban.entities.Board;
import com.waly.kanban.entities.Card;
import com.waly.kanban.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardDTO {

    private Long id;
    private String title;
    private Integer totalCards;
    private List<CardDTO> cards = new ArrayList<>();

    public BoardDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.totalCards = board.getTotalCards();
        board.getCards().forEach(this::addCardSorted);
    }

    public void addCard(CardDTO card) {
        this.cards.add(card);
        cards.sort(Comparator.comparingInt(CardDTO::getPosition));
    }

    private void addCardSorted(Card card) {
        CardDTO cardDTO = new CardDTO(card);
        addCard(cardDTO);
    }
}
