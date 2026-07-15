package com.bloquinho.category.domain;

public record ProfessionalCategory(
    String publicId,
    String name,
    String slug,
    String description,
    boolean active
) {
}
