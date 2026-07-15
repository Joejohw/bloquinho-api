package com.bloquinho.professional.application;

import com.bloquinho.professional.domain.Professional;
import com.bloquinho.professional.domain.ProfessionalRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListProfessionalsByCategoryUseCase {
    private final ProfessionalRepository repository;

    public ListProfessionalsByCategoryUseCase(ProfessionalRepository repository) {
        this.repository = repository;
    }

    public List<Professional> execute(String slug) {
        return List.copyOf(repository.findAllActiveByCategorySlugOrderByName(slug));
    }
}
