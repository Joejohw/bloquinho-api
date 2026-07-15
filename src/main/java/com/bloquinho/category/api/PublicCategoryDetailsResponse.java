package com.bloquinho.category.api;

import com.bloquinho.category.application.PublicCategoryDetails;
import com.bloquinho.professional.api.PublicProfessionalResponse;
import java.util.List;

public record PublicCategoryDetailsResponse(
    String publicId,
    String name,
    String slug,
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
