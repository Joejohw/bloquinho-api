package com.bloquinho.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bloquinho.category.domain.ProfessionalCategory;
import com.bloquinho.category.domain.ProfessionalCategoryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListPublicCategoriesUseCaseTest {
    private final ProfessionalCategoryRepository repository = mock(ProfessionalCategoryRepository.class);
    private final ListPublicCategoriesUseCase useCase = new ListPublicCategoriesUseCase(repository);

    @Test
    void returnsRepositoryContentInTheProvidedOrder() {
        var electrical = category("electrical", "Elétrica", "eletrica");
        var plumbing = category("plumbing", "Hidráulica", "hidraulica");
        when(repository.findAllActiveOrderByName()).thenReturn(List.of(electrical, plumbing));

        assertThat(useCase.execute()).containsExactly(electrical, plumbing);
    }

    @Test
    void acceptsAnEmptyList() {
        when(repository.findAllActiveOrderByName()).thenReturn(List.of());

        assertThat(useCase.execute()).isEmpty();
    }

    private ProfessionalCategory category(String publicId, String name, String slug) {
        return new ProfessionalCategory(publicId, name, slug, "Description", true);
    }
}
