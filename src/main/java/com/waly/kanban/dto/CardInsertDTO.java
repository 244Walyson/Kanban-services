package com.waly.kanban.dto;

import com.waly.kanban.entities.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardInsertDTO {

    private Long id;
    private String title;
    private String description;
    private Long board;

}
