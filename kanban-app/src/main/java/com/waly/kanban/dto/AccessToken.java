package com.waly.kanban.dto;

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
