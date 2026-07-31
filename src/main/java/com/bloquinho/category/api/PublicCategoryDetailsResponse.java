package com.bloquinho.category.api;

import com.bloquinho.category.application.PublicCategoryDetails;
import com.bloquinho.professional.api.PublicProfessionalResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PublicCategoryDetailsResponse(
    @Schema(example = "Ctg000000000000000001")
    String publicId,
    @Schema(example = "Elétrica")
    String name,
    @Schema(example = "eletrica")
    String slug,
    @Schema(example = "Instalações, reparos e manutenção elétrica.")
    String description,
    List<PublicProfessionalResponse> professionals
) {
    static PublicCategoryDetailsResponse from(PublicCategoryDetails details) {
        return new PublicCategoryDetailsResponse(
            details.publicId(), details.name(), details.slug(), details.description(),
            details.professionals().stream().map(PublicProfessionalResponse::from).toList()
        );
    }
}
