package com.bloquinho.professional.application;

import java.util.List;

public record PublicProfessionalDetails(
    String publicId,
    String name,
    String businessName,
    String description,
    String whatsapp,
    String instagram,
    String city,
    String state,
    List<PublicProfessionalCategory> categories
) {
    public PublicProfessionalDetails {
        categories = List.copyOf(categories);
    }
}
