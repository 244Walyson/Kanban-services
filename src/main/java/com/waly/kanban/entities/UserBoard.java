package com.waly.kanban.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user_board")
public class UserBoard {

    @EmbeddedId
    private UserBoardPK id = new UserBoardPK();
    private boolean isAdmin;

    public UserBoard() {
    }

    public UserBoard(UserBoardPK id, boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public UserBoardPK getId() {
        return id;
    }

    public void setId(UserBoardPK id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
