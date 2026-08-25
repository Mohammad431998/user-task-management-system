package com.mohammad.userandtaskmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if ("User not found".equals(ex.getMessage())
                || "Role not found".equals(ex.getMessage())
                || "Task not found".equals(ex.getMessage())
                || "Comment not found".equals(ex.getMessage())
                || "Notification not found".equals(ex.getMessage())) {

            status = HttpStatus.NOT_FOUND;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}