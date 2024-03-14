package com.waly.kanban.dto;

import com.waly.kanban.entities.Card;
import com.waly.kanban.entities.User;
import com.waly.kanban.entities.UserBoard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String imgUrl;
    private String bio;
    private List<CardDTO> cards = new ArrayList<>();
    private List<BoardDTO> boards = new ArrayList<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imgUrl = user.getImgUrl();
        this.bio = user.getBio();
        this.cards = user.getCards().stream().map(x -> new CardDTO(x)).toList();
        this.boards = user.getBoards().stream().map(x -> new BoardDTO(x)).toList();
    }
}
