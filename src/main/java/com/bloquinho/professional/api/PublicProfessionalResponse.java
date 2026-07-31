package com.bloquinho.professional.api;

import com.bloquinho.professional.domain.Professional;
import io.swagger.v3.oas.annotations.media.Schema;

public record PublicProfessionalResponse(
    @Schema(example = "Pro000000000000000001")
    String publicId,
    @Schema(example = "Carlos Elétrica Residencial")
    String name,
    @Schema(example = "Carlos Elétrica Demo")
    String businessName,
    @Schema(example = "Serviços fictícios de instalações e reparos elétricos residenciais.")
    String description,
    @Schema(example = "5500000000001")
    String whatsapp,
    @Schema(example = "https://instagram.com/bloquinho_demo_eletrica")
    String instagram,
    @Schema(example = "Campinas")
    String city,
    @Schema(example = "SP")
    String state
) {
    public static PublicProfessionalResponse from(Professional professional) {
        return new PublicProfessionalResponse(
            professional.publicId(), professional.name(), professional.businessName(),
            professional.description(), professional.whatsapp(), professional.instagram(),
            professional.city(), professional.state()
        );
    }
}
