package com.waly.kanban.dto;

import com.waly.kanban.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String imgUrl;
    private String bio;
    private List<CardDTO> cards = new ArrayList<>();
    private List<TeamDTO> teams = new ArrayList<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.imgUrl = user.getImgUrl();
        this.bio = user.getBio();
        this.cards = user.getCards().stream().map(x -> new CardDTO(x)).toList();
        this.teams = user.getBoards().stream().map(x -> new TeamDTO(x)).toList();
    }
}
