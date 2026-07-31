package com.bloquinho.professional.infrastructure;

import com.bloquinho.professional.domain.Professional;
import com.bloquinho.professional.domain.ProfessionalProfile;
import com.bloquinho.professional.domain.ProfessionalProfileCategory;
import com.bloquinho.professional.domain.ProfessionalRepository;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<ProfessionalProfile> findActiveProfileByPublicId(String publicId) {
        return repository.findByPublicIdAndActiveTrue(publicId)
            .map(entity -> new ProfessionalProfile(
                toDomain(entity),
                repository.findAllActiveProfileCategoriesOrderByName(publicId).stream()
                    .map(this::toProfileCategory)
                    .toList()
            ));
    }

    private ProfessionalProfileCategory toProfileCategory(ProfessionalProfileCategoryView category) {
        return new ProfessionalProfileCategory(
            category.getPublicId(),
            category.getName(),
            category.getSlug(),
            category.getActive()
        );
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
