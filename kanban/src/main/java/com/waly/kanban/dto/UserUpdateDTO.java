package com.waly.kanban.dto;

import com.waly.kanban.services.validation.UserUpdateValid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@UserUpdateValid
public class UserUpdateDTO {

    private Long id;
    private String name;
    private String nickname;
    @Email(message = "Email invalido")
    private String email;
    private String imgUrl;
    private String bio;

}
