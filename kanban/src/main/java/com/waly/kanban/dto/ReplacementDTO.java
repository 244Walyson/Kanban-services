package com.waly.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReplacementDTO {

    private Integer sourceIndex;
    private Integer destinationIndex;
}
