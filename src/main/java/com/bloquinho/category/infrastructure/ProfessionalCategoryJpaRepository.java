package com.bloquinho.category.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProfessionalCategoryJpaRepository
    extends JpaRepository<ProfessionalCategoryJpaEntity, Long> {

    List<ProfessionalCategoryJpaEntity> findAllByActiveTrueOrderByNameAsc();
    Optional<ProfessionalCategoryJpaEntity> findBySlugAndActiveTrue(String slug);
}
