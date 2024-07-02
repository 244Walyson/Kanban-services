package com.waly.auth_service.projections;
public interface UserDetailsProjection {

        String getUsername();
        String getPassword();
        Long getRoleId();
        String getAuthority();
}

