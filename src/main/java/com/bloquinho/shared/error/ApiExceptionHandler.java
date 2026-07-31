package com.bloquinho.shared.error;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) ProblemDetail validation() { return problem(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid."); }
    @ExceptionHandler(ConstraintViolationException.class) ProblemDetail constraintViolation(ConstraintViolationException ex) {
        var detail = ex.getConstraintViolations().stream()
            .map(violation -> violation.getMessage())
            .distinct()
            .sorted()
            .collect(Collectors.joining(" "));
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", detail);
    }
    @ExceptionHandler(ResourceNotFoundException.class) ProblemDetail notFound(ResourceNotFoundException ex) { return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage()); }
    @ExceptionHandler(Exception.class) ProblemDetail unexpected() { return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", "The request could not be completed."); }
    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        var value = ProblemDetail.forStatusAndDetail(status, detail);
        value.setType(URI.create("about:blank"));
        value.setTitle(title);
        return value;
    }
}
