package com.bloquinho.shared.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ApiExceptionHandlerTest {
    @Test
    void returnsBadRequestProblemDetailForConstraintViolations() {
        ConstraintViolation<?> slugViolation = mock(ConstraintViolation.class);
        ConstraintViolation<?> otherViolation = mock(ConstraintViolation.class);
        when(slugViolation.getMessage()).thenReturn("Slug inválido.");
        when(otherViolation.getMessage()).thenReturn("A requisição é inválida.");
        var exception = new ConstraintViolationException(Set.of(slugViolation, otherViolation));

        var result = new ApiExceptionHandler().constraintViolation(exception);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getType()).hasToString("about:blank");
        assertThat(result.getTitle()).isEqualTo("Invalid request");
        assertThat(result.getDetail()).isEqualTo("A requisição é inválida. Slug inválido.");
        assertThat(result.getProperties()).isNullOrEmpty();
    }
}
