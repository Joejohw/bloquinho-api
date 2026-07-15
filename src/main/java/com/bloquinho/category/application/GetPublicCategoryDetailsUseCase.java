package com.bloquinho.category.application;

import com.bloquinho.category.domain.ProfessionalCategoryRepository;
import com.bloquinho.professional.application.ListProfessionalsByCategoryUseCase;
import com.bloquinho.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetPublicCategoryDetailsUseCase {
    private final ProfessionalCategoryRepository categoryRepository;
    private final ListProfessionalsByCategoryUseCase listProfessionals;

    public GetPublicCategoryDetailsUseCase(
        ProfessionalCategoryRepository categoryRepository,
        ListProfessionalsByCategoryUseCase listProfessionals
    ) {
        this.categoryRepository = categoryRepository;
        this.listProfessionals = listProfessionals;
    }

    public PublicCategoryDetails execute(String slug) {
        var category = categoryRepository.findActiveBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found."));
        var professionals = listProfessionals.execute(slug);
        return new PublicCategoryDetails(
            category.publicId(), category.name(), category.slug(),
            category.description(), professionals
        );
    }
}
