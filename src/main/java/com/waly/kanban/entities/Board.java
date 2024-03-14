package com.waly.kanban.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "tb_board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private Set<Card> cards = new HashSet<>();
    private Integer totalCards;
    @OneToMany(mappedBy = "id.board")
    private Set<UserBoard> users = new HashSet<>();

    public Board(Long id, String title, Integer totalCards) {
        this.id = id;
        this.title = title;
        this.totalCards = totalCards;
    }

    public Board() {
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

    public Integer getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(Integer totalCards) {
        this.totalCards = totalCards;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public List<User> getUsers() {
        return this.users.stream().map(x -> x.getId().getUser()).toList();
    }
}
