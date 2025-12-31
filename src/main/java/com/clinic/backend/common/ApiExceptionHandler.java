package com.clinic.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {

        // Bizim controllerlardaki custom exception’lar:
        // NotFoundException -> 404
        // BadRequestException -> 400
        // ConflictException -> 409
        // Bunların üzerindeki @ResponseStatus'u burada manuel vericez json olarak istenilen mesaj dönecek.

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String name = ex.getClass().getSimpleName();
        if (name.contains("NotFoundException")) status = HttpStatus.NOT_FOUND;
        else if (name.contains("BadRequestException")) status = HttpStatus.BAD_REQUEST;
        else if (name.contains("ConflictException")) status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status).body(Map.of(
                "message", ex.getMessage(),
                "status", status.value()
        ));
    }
}