package com.waly.kanban.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardUpdateDTO {

    private Long id;
    @NotNull(message = "O titulo do card não deve ser null")
    @NotEmpty(message = "O titulo do card não deve ser vazio")
    @NotBlank(message = "O titulo do card não deve estar em branco")
    private String title;
    @NotNull(message = "A descrição do card não deve ser null")
    @Size(min = 10,message = "A descrição do card deve ter no minimo 10 caracteres")
    @NotBlank(message = "A descrição do card não deve estar em branco")
    private String description;
}
