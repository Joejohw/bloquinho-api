package com.bloquinho.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.bloquinho.category.domain.ProfessionalCategory;
import com.bloquinho.category.domain.ProfessionalCategoryRepository;
import com.bloquinho.professional.application.ListProfessionalsByCategoryUseCase;
import com.bloquinho.professional.domain.Professional;
import com.bloquinho.shared.error.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetPublicCategoryDetailsUseCaseTest {
    private final ProfessionalCategoryRepository categories = mock(ProfessionalCategoryRepository.class);
    private final ListProfessionalsByCategoryUseCase professionals = mock(ListProfessionalsByCategoryUseCase.class);
    private final GetPublicCategoryDetailsUseCase useCase =
        new GetPublicCategoryDetailsUseCase(categories, professionals);

    @Test
    void returnsTheCategoryAndProfessionalsInRepositoryOrder() {
        var category = category();
        var first = professional("Ana Demo");
        var second = professional("Bruno Demo");
        when(categories.findActiveBySlug("eletrica")).thenReturn(Optional.of(category));
        when(professionals.execute("eletrica")).thenReturn(List.of(first, second));

        var result = useCase.execute("eletrica");

        assertThat(result.slug()).isEqualTo("eletrica");
        assertThat(result.professionals()).containsExactly(first, second);
    }

    @Test
    void acceptsAnEmptyProfessionalList() {
        when(categories.findActiveBySlug("eletrica")).thenReturn(Optional.of(category()));
        when(professionals.execute("eletrica")).thenReturn(List.of());

        assertThat(useCase.execute("eletrica").professionals()).isEmpty();
    }

    @Test
    void rejectsAnUnknownOrInactiveCategory() {
        when(categories.findActiveBySlug("inactive")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("inactive"))
            .isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(professionals);
    }

    private ProfessionalCategory category() {
        return new ProfessionalCategory(
            "Ctg000000000000000001", "Elétrica", "eletrica", "Description", true
        );
    }

    private Professional professional(String name) {
        return new Professional(
            "Pro000000000000000001", name, null, "Description", null,
            "5500000000001", null, null, "Campinas", "SP", true
        );
    }
}
