package com.bloquinho.professional.api;

import com.bloquinho.professional.application.PublicProfessionalDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PublicProfessionalDetailsResponse(
    @Schema(example = "Pro000000000000000002")
    String publicId,
    @Schema(example = "Lumen Instalações")
    String name,
    @Schema(example = "Lumen Instalações Demo")
    String businessName,
    @Schema(example = "Demonstração de serviços elétricos e instalação de climatização.")
    String description,
    @Schema(example = "5500000000002")
    String whatsapp,
    @Schema(example = "https://instagram.com/bloquinho_demo_lumen")
    String instagram,
    @Schema(example = "Campinas")
    String city,
    @Schema(example = "SP")
    String state,
    List<PublicProfessionalCategoryResponse> categories
) {
    static PublicProfessionalDetailsResponse from(PublicProfessionalDetails details) {
        return new PublicProfessionalDetailsResponse(
            details.publicId(),
            details.name(),
            details.businessName(),
            details.description(),
            details.whatsapp(),
            details.instagram(),
            details.city(),
            details.state(),
            details.categories().stream().map(PublicProfessionalCategoryResponse::from).toList()
        );
    }
}
