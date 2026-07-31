package com.bloquinho.catalog.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicStatusController {
    @Operation(
        summary = "Get public application status",
        description = "Returns the fixed Bloquinho API application status. This is distinct from dependency health exposed by Actuator."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Application status",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"data\":{\"application\":\"bloquinho-api\",\"status\":\"UP\"}}"
            )
        )
    )
    @ApiResponse(responseCode = "405", description = "HTTP method not supported")
    @GetMapping("/status")
    public Map<String, Map<String, String>> status() {
        return Map.of("data", Map.of("application", "bloquinho-api", "status", "UP"));
    }
}
