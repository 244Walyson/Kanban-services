package com.waly.kanban.projections;
public interface UserDetailsProjection {

        String getUsername();
        String getPassword();
        Long getRoleId();
        String getAuthority();
}

