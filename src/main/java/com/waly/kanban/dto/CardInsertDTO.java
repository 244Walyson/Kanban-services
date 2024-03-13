package com.waly.kanban.dto;

import com.waly.kanban.entities.Card;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardInsertDTO {

    private Long id;
    @NotNull(message = "O titulo do card não deve ser null")
    @NotEmpty(message = "O titulo do card não deve ser vazio")
    @NotBlank(message = "O titulo do card não deve estar em branco")
    private String title;
    @NotNull(message = "A descrição do card não deve ser null")
    @Size(min = 10,message = "A descrição do card deve ter no minimo 10 caracteres")
    @NotBlank(message = "A descrição do card não deve estar em branco")
    private String description;
    @NotNull(message = "O id do board ao qual o card pertence deve ser informado")
    @Positive(message = "O id do board ao qual o card pertence deve ser informado")
    @Digits(integer = 10, fraction = 0, message = "O id do board ao qual o card pertence deve ser um número inteiro")
    private Long boardId;

}
