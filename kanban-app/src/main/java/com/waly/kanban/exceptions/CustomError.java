package com.waly.kanban.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomError {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;
    private String message;

}