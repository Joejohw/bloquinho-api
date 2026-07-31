package com.bloquinho.professional.application;

import com.bloquinho.professional.domain.ProfessionalRepository;
import com.bloquinho.shared.error.ResourceNotFoundException;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class GetPublicProfessionalDetailsUseCase {
    private final ProfessionalRepository repository;

    public GetPublicProfessionalDetailsUseCase(ProfessionalRepository repository) {
        this.repository = repository;
    }

    public PublicProfessionalDetails execute(String publicId) {
        var profile = repository.findActiveProfileByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Professional not found."));
        var professional = profile.professional();
        var categories = profile.categories().stream()
            .filter(category -> category.active())
            .sorted(Comparator.comparing(category -> category.name()))
            .map(category -> new PublicProfessionalCategory(
                category.publicId(), category.name(), category.slug()
            ))
            .toList();
        return new PublicProfessionalDetails(
            professional.publicId(),
            professional.name(),
            professional.businessName(),
            professional.description(),
            professional.whatsapp(),
            professional.instagram(),
            professional.city(),
            professional.state(),
            categories
        );
    }
}
