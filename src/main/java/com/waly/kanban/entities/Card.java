package com.waly.kanban.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer cardPosition;
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    public Card() {
    }

    public Card(Long id, String title, String description, Integer cardPosition, Board board) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cardPosition = cardPosition;
        this.board = board;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCardPosition() {
        return cardPosition;
    }

    public void setCardPosition(Integer cardPosition) {
        this.cardPosition = cardPosition;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
