package com.bloquinho.category.infrastructure;

import com.bloquinho.category.domain.ProfessionalCategory;
import com.bloquinho.category.domain.ProfessionalCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ProfessionalCategoryRepositoryAdapter implements ProfessionalCategoryRepository {
    private final ProfessionalCategoryJpaRepository repository;

    public ProfessionalCategoryRepositoryAdapter(ProfessionalCategoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProfessionalCategory> findAllActiveOrderByName() {
        return repository.findAllByActiveTrueOrderByNameAsc().stream()
            .map(this::toDomain)
            .toList();
    }

    private ProfessionalCategory toDomain(ProfessionalCategoryJpaEntity entity) {
        return new ProfessionalCategory(
            entity.getPublicId(),
            entity.getName(),
            entity.getSlug(),
            entity.getDescription(),
            entity.isActive()
        );
    }
}
