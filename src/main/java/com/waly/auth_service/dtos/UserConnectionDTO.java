package com.waly.auth_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.waly.auth_service.entities.UserConnection;
import lombok.ToString;

@ToString
public class UserConnectionDTO {

    @JsonProperty("user1")
    private UserDTO user1;
    @JsonProperty("user2")
    private UserDTO user2;
    @JsonProperty("status")
    private boolean status = false;

    public UserConnectionDTO() {
    }

    public UserConnectionDTO(UserDTO user1, UserDTO user2, boolean status) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
    }

    public UserConnectionDTO(UserConnection userConn) {
        this.user1 = new UserDTO(userConn.getId().getUser1());
        this.user2 = new UserDTO(userConn.getId().getUser2());
        this.status = userConn.isStatus();
    }
}
