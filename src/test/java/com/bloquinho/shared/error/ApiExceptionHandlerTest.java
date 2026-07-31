package com.bloquinho.shared.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @Test
    void returnsNotFoundProblemDetailForMissingMvcResource() {
        var exception = new NoResourceFoundException(
            HttpMethod.GET,
            "/api/v1/public/recurso-inexistente",
            "No static resource"
        );

        var result = new ApiExceptionHandler().mvcNotFound();

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getType()).hasToString("about:blank");
        assertThat(result.getTitle()).isEqualTo("Resource not found");
        assertThat(result.getDetail()).isEqualTo("The requested resource was not found.");
        assertThat(result.getProperties()).isNullOrEmpty();
        assertThat(result.toString())
            .doesNotContain("NoResourceFoundException", "stackTrace", exception.getResourcePath());
    }

    @Test
    void returnsMethodNotAllowedProblemDetailAndPreservesAllowHeader() {
        var exception = new HttpRequestMethodNotSupportedException("POST", List.of("GET"));

        var response = new ApiExceptionHandler().methodNotAllowed(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(405);
        assertThat(response.getHeaders().getAllow()).containsExactly(HttpMethod.GET);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(405);
        assertThat(response.getBody().getType()).hasToString("about:blank");
        assertThat(response.getBody().getTitle()).isEqualTo("Method not allowed");
        assertThat(response.getBody().getDetail())
            .isEqualTo("The HTTP method is not supported for this resource.");
        assertThat(response.getBody().getProperties()).isNullOrEmpty();
        assertThat(response.getBody().toString())
            .doesNotContain("HttpRequestMethodNotSupportedException", "stackTrace");
    }
}
