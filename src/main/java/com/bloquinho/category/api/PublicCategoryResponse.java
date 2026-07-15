package com.bloquinho.category.api;

import com.bloquinho.category.domain.ProfessionalCategory;

public record PublicCategoryResponse(
    String publicId,
    String name,
    String slug,
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
