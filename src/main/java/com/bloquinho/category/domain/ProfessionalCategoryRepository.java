package com.bloquinho.category.domain;

import java.util.List;
import java.util.Optional;

public interface ProfessionalCategoryRepository {
    List<ProfessionalCategory> findAllActiveOrderByName();
    Optional<ProfessionalCategory> findActiveBySlug(String slug);
}
