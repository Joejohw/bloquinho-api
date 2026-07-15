package com.bloquinho.shared.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) ProblemDetail validation() { return problem(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid."); }
    @ExceptionHandler(ResourceNotFoundException.class) ProblemDetail notFound(ResourceNotFoundException ex) { return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage()); }
    @ExceptionHandler(Exception.class) ProblemDetail unexpected() { return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", "The request could not be completed."); }
    private ProblemDetail problem(HttpStatus status, String title, String detail) { var value = ProblemDetail.forStatusAndDetail(status, detail); value.setTitle(title); return value; }
}
