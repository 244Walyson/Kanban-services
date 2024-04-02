package com.kanban.chat.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtDTO {

    private int id;
    private String name;
    private String email;

    public static JwtDTO getUser(Claims claims){
        try {
            return new ObjectMapper().convertValue(claims.get("authUser"), JwtDTO.class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
