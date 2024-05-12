package com.waly.kanban.dto;

import com.waly.kanban.entities.UserConnection;
import com.waly.kanban.entities.UserConnectionPK;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

@ToString
public class UserConnectionDTO {

    private UserMinDTO user1;
    private UserMinDTO user2;
    private boolean status = false;

    public UserConnectionDTO() {
    }

    public UserConnectionDTO(UserMinDTO user1, UserMinDTO user2, boolean status) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
    }

    public UserConnectionDTO(UserConnection userConn) {
        this.user1 = new UserMinDTO(userConn.getId().getUser1());
        this.user2 = new UserMinDTO(userConn.getId().getUser2());
        this.status = userConn.isStatus();
    }
}
