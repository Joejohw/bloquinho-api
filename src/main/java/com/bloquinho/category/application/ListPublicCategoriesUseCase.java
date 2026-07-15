package com.bloquinho.category.application;

import com.bloquinho.category.domain.ProfessionalCategory;
import com.bloquinho.category.domain.ProfessionalCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListPublicCategoriesUseCase {
    private final ProfessionalCategoryRepository repository;

    public ListPublicCategoriesUseCase(ProfessionalCategoryRepository repository) {
        this.repository = repository;
    }

    public List<ProfessionalCategory> execute() {
        return List.copyOf(repository.findAllActiveOrderByName());
    }
}
