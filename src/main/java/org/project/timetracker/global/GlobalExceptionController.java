package org.project.timetracker.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleAllCustom(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(e.getMessage()));
    }
}
