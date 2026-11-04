package ru.practicum.ewm.stats.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.util.Map;

@RestControllerAdvice
public class StatsExceptionHandler {
    @ExceptionHandler({DateTimeException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleInvalidParameters(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid date range or date-time format"));
    }
}
