package com.waly.kanban.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String imgUrl;
    @Column(columnDefinition = "TEXT")
    private String bio;
    @OneToMany(mappedBy = "id.user")
    private Set<UserBoard> boards = new HashSet<>();
    @ManyToOne
    private Card card;

    public User() {
    }

    public User(Long id, String name, String username, String email, String imgUrl, String bio, Card card) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.imgUrl = imgUrl;
        this.bio = bio;
        this.card = card;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Board> getBoards(){
        return boards.stream().map(x -> x.getId().getBoard()).toList();
    }
}
