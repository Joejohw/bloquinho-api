package com.bloquinho.professional.api;

import com.bloquinho.professional.domain.Professional;

public record PublicProfessionalResponse(
    String publicId,
    String name,
    String businessName,
    String description,
    String whatsapp,
    String instagram,
    String city,
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
