package com.waly.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddUserDTO {

    private Long userId;
    private Boolean isAdmin;
}