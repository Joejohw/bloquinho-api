package com.bloquinho.category.api;

import com.bloquinho.category.domain.ProfessionalCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record PublicCategoryResponse(
    @Schema(example = "Ctg000000000000000001")
    String publicId,
    @Schema(example = "Elétrica")
    String name,
    @Schema(example = "eletrica")
    String slug,
    @Schema(example = "Instalações, reparos e manutenção elétrica.")
    String description
) {
    static PublicCategoryResponse from(ProfessionalCategory category) {
        return new PublicCategoryResponse(
            category.publicId(),
            category.name(),
            category.slug(),
            category.description()
        );
    }
}
