package com.bloquinho.professional.domain;

public record ProfessionalProfileCategory(
    String publicId,
    String name,
    String slug,
    boolean active
) {
}
