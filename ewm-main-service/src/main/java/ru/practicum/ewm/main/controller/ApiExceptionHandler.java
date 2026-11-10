package ru.practicum.ewm.main.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.main.exception.ApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {


 @ExceptionHandler(ApiException.class) public ResponseEntity < Map<String, String>  >  handle(ApiException e) {

  return ResponseEntity.status(e.getStatus()).body(Map.of("status", e.getStatus().name(), "reason", "For the requested operation the conditions are not met.", "message", e.getMessage(), "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

}

 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity < Map<String, String>  >  bad(IllegalArgumentException e) {

  return ResponseEntity.badRequest().body(Map.of("status", "BAD_REQUEST", "reason", "Incorrectly made request.", "message", e.getMessage() == null ? "Invalid request" : e.getMessage(), "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

}

 @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
 public ResponseEntity < Map<String, String>  >  conflict(org.springframework.dao.DataIntegrityViolationException e) {

  return ResponseEntity.status(409).body(Map.of("status", "CONFLICT", "reason", "Integrity constraint has been violated.", "message", "The requested data conflicts with existing data.", "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

}

}
