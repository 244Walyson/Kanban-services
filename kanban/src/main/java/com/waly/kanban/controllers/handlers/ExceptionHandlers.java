package com.waly.kanban.controllers.handlers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.waly.kanban.exceptions.CustomError;
import com.waly.kanban.exceptions.DatabaseException;
import com.waly.kanban.exceptions.NotFoundException;
import com.waly.kanban.exceptions.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomError> notFoundException(HttpServletRequest request, NotFoundException e){
        CustomError error = new CustomError();
        int status = HttpStatus.NOT_FOUND.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Recurso não encontrado");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError error = new ValidationError();
        error.setError("Validate Exception");
        error.setPath(request.getRequestURI());
        error.setStatus(status.value());
        error.setTimestamp(Instant.now());
        for (FieldError f : e.getBindingResult().getFieldErrors()){
            error.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status.value()).body(error);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> databaseException(DatabaseException e, HttpServletRequest request){
        CustomError error = new CustomError();
        int status = HttpStatus.BAD_REQUEST.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Database Exception");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<CustomError> databaseException(AmazonS3Exception e, HttpServletRequest request){
        CustomError error = new CustomError();
        int status = HttpStatus.BAD_REQUEST.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Amazon Exception");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(AmazonClientException.class)
    public ResponseEntity<CustomError> databaseException(AmazonClientException e, HttpServletRequest request){
        CustomError error = new CustomError();
        int status = HttpStatus.BAD_REQUEST.value();
        error.setStatus(status);
        error.setMessage(e.getMessage());
        error.setTimestamp(Instant.now());
        error.setError("Amazon Exception");
        error.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(error);

    }
}
