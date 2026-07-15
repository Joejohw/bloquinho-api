package com.bloquinho.category.application;

import com.bloquinho.professional.domain.Professional;
import java.util.List;

public record PublicCategoryDetails(
    String publicId,
    String name,
    String slug,
    String description,
    List<Professional> professionals
) {
}
