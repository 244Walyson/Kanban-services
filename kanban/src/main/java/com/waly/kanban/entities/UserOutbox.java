package com.waly.kanban.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_user_outbox")
public class UserOutbox {

    @Id
    private Long id;
    private String name;
    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String email;
    private String imgUrl;
    private Long teamId;

    public UserOutbox(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.imgUrl = user.getImgUrl();
    }
}
