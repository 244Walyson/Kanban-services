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
public class UserMinDTO {

    private Long id;
    private String name;
    private String email;
    public UserMinDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getUsername();
    }
}
