package com.bloquinho.category.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProfessionalCategoryJpaRepository
    extends JpaRepository<ProfessionalCategoryJpaEntity, Long> {

    List<ProfessionalCategoryJpaEntity> findAllByActiveTrueOrderByNameAsc();
}
