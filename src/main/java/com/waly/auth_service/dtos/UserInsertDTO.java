package com.waly.auth_service.dtos;

import com.waly.auth_service.services.validation.UserInsertValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@UserInsertValid
public class UserInsertDTO {

    private String name;
    @NotNull
    private String nickname;
    @Email(message = "Email invalido")
    private String email;
    private String imgUrl;
    private String bio;
    @NotNull(message = "Senha deve ser informada")
    @NotEmpty(message = "Senha deve ser informada")
    @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
    private String password;

}
