package com.waly.kanban.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
    private boolean done;
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToMany
    @JoinTable(name = "tb_user_card",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "card_id"))
    private Set<User> collaborators;

    public Card() {
    }

    public Card(Long id, String title, String description, Integer cardPosition, Board board, boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cardPosition = cardPosition;
        this.board = board;
        this.done = done;
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Set<User> getCollaborators() {
        return collaborators;
    }

    public void addCollaborator(User collaborator) {
        this.collaborators.add(collaborator);
    }
}
