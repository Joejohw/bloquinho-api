package com.bloquinho.professional.api;

import com.bloquinho.professional.application.PublicProfessionalCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record PublicProfessionalCategoryResponse(
    @Schema(example = "Ctg000000000000000009")
    String publicId,
    @Schema(example = "Ar-condicionado")
    String name,
    @Schema(example = "ar-condicionado")
    String slug
) {
    static PublicProfessionalCategoryResponse from(PublicProfessionalCategory category) {
        return new PublicProfessionalCategoryResponse(
            category.publicId(), category.name(), category.slug()
        );
    }
}
