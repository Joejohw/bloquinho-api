package com.bloquinho.professional.api;

import com.bloquinho.professional.application.GetPublicProfessionalDetailsUseCase;
import com.bloquinho.shared.id.PublicIdGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Pattern;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/professionals")
@Validated
public class PublicProfessionalController {
    private final GetPublicProfessionalDetailsUseCase getDetails;

    public PublicProfessionalController(GetPublicProfessionalDetailsUseCase getDetails) {
        this.getDetails = getDetails;
    }

    @Operation(
        summary = "Get an active professional's public profile",
        description = "Returns only public fields and active categories. Missing and inactive professionals are indistinguishable."
    )
    @ApiResponse(responseCode = "200", description = "Active professional public profile")
    @ApiResponse(responseCode = "400", description = "Malformed public identifier")
    @ApiResponse(responseCode = "404", description = "Active professional not found")
    @ApiResponse(responseCode = "405", description = "HTTP method not supported")
    @GetMapping("/{publicId}")
    public Map<String, PublicProfessionalDetailsResponse> details(
        @Parameter(
            description = "URL-safe 21-character professional public identifier",
            example = "Pro000000000000000002"
        )
        @PathVariable
        @Pattern(
            regexp = PublicIdGenerator.PATTERN,
            message = "Identificador público inválido."
        )
        String publicId
    ) {
        return Map.of("data", PublicProfessionalDetailsResponse.from(getDetails.execute(publicId)));
    }
}
