package com.waly.auth_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccessToken {

    private String access_token;
    private String token_type;
    private Integer expires_in;
}
