package com.waly.auth_service.controllers.handlers;

import com.waly.auth_service.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class ExceptionHandlers {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> notFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        CustomError error = new CustomError();
        int status = HttpStatus.NOT_FOUND.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Resource Not Found");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError error = new ValidationError();
        error.setError("Validate Exception");
        error.setPath(request.getRequestURI());
        error.setStatus(status.value());
        error.setTimestamp(Instant.now());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            error.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status.value()).body(error);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> databaseException(DatabaseException e, HttpServletRequest request) {
        CustomError error = new CustomError();
        int status = HttpStatus.BAD_REQUEST.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Database Exception");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<CustomError> validateException(ValidateException e, HttpServletRequest request) {
        CustomError error = new CustomError();
        int status = HttpStatus.BAD_REQUEST.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Validate Exception");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
