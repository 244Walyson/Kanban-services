package com.waly.kanban.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user_team")
public class UserTeam {

    @EmbeddedId
    private UserTeamPK id = new UserTeamPK();
    private boolean isAdmin;

    public UserTeam() {
    }

    public UserTeam(UserTeamPK id, boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public UserTeamPK getId() {
        return id;
    }

    public void setId(UserTeamPK id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
