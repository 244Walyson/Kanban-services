package com.waly.auth_service.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Embeddable
public class UserConnectionPK {

    @ManyToOne
    @JoinColumn(name = "user_id1")
    private User user1;
    @ManyToOne
    @JoinColumn(name = "user_id2")
    private User user2;

    public UserConnectionPK() {
    }

    public UserConnectionPK(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }
}
