package com.waly.auth_service.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user_connection")
public class UserConnection {

    @EmbeddedId
    private UserConnectionPK id = new UserConnectionPK();
    private boolean status = false;

    public UserConnection() {
    }

    public UserConnection(UserConnectionPK id, boolean status) {
        this.id = id;
        this.status = status;
    }

    public UserConnectionPK getId() {
        return id;
    }

    public void setId(UserConnectionPK id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
