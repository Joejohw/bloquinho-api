package com.bloquinho.professional.infrastructure;

import com.bloquinho.professional.domain.Professional;
import com.bloquinho.professional.domain.ProfessionalRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ProfessionalRepositoryAdapter implements ProfessionalRepository {
    private final ProfessionalJpaRepository repository;

    public ProfessionalRepositoryAdapter(ProfessionalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Professional> findAllActiveByCategorySlugOrderByName(String slug) {
        return repository.findAllActiveByCategorySlugOrderByName(slug).stream()
            .map(this::toDomain)
            .toList();
    }

    private Professional toDomain(ProfessionalJpaEntity entity) {
        return new Professional(
            entity.getPublicId(), entity.getName(), entity.getBusinessName(),
            entity.getDescription(), entity.getPhone(), entity.getWhatsapp(),
            entity.getEmail(), entity.getInstagram(), entity.getCity(),
            entity.getState(), entity.isActive()
        );
    }
}
