package com.bloquinho.professional.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bloquinho.professional.domain.Professional;
import com.bloquinho.professional.domain.ProfessionalProfile;
import com.bloquinho.professional.domain.ProfessionalProfileCategory;
import com.bloquinho.professional.domain.ProfessionalRepository;
import com.bloquinho.shared.error.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetPublicProfessionalDetailsUseCaseTest {
    private final ProfessionalRepository repository = mock(ProfessionalRepository.class);
    private final GetPublicProfessionalDetailsUseCase useCase =
        new GetPublicProfessionalDetailsUseCase(repository);

    @Test
    void returnsOnlyPublicFieldsAndActiveCategoriesInNameOrder() {
        var inactive = category("Ctg000000000000000003", "Pintura", "pintura", false);
        var electrical = category("Ctg000000000000000001", "Elétrica", "eletrica", true);
        var airConditioning = category(
            "Ctg000000000000000009", "Ar-condicionado", "ar-condicionado", true
        );
        when(repository.findActiveProfileByPublicId("Pro000000000000000002"))
            .thenReturn(Optional.of(new ProfessionalProfile(
                professional(),
                List.of(inactive, electrical, airConditioning)
            )));

        var result = useCase.execute("Pro000000000000000002");

        assertThat(result.publicId()).isEqualTo("Pro000000000000000002");
        assertThat(result.name()).isEqualTo("Lumen Instalações");
        assertThat(result.businessName()).isEqualTo("Lumen Instalações Demo");
        assertThat(result.whatsapp()).isEqualTo("5500000000002");
        assertThat(result.categories()).extracting("name")
            .containsExactly("Ar-condicionado", "Elétrica");
        assertThat(PublicProfessionalDetails.class.getRecordComponents())
            .extracting(component -> component.getName())
            .doesNotContain("id", "active", "phone", "email", "createdAt", "updatedAt");
    }

    @Test
    void returnsAnEmptyCategoryList() {
        when(repository.findActiveProfileByPublicId("Pro000000000000000002"))
            .thenReturn(Optional.of(new ProfessionalProfile(professional(), List.of())));

        assertThat(useCase.execute("Pro000000000000000002").categories()).isEmpty();
    }

    @Test
    void rejectsUnknownProfessional() {
        when(repository.findActiveProfileByPublicId("Pro000000000000000099"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("Pro000000000000000099"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Professional not found.");
    }

    @Test
    void treatsInactiveProfessionalAsNotFound() {
        when(repository.findActiveProfileByPublicId("Pro000000000000000002"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("Pro000000000000000002"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Professional not found.");
    }

    private Professional professional() {
        return new Professional(
            "Pro000000000000000002",
            "Lumen Instalações",
            "Lumen Instalações Demo",
            "Demonstração de serviços elétricos e instalação de climatização.",
            "+55 00 00000-0002",
            "5500000000002",
            "lumen@example.com",
            "https://instagram.com/bloquinho_demo_lumen",
            "Campinas",
            "SP",
            true
        );
    }

    private ProfessionalProfileCategory category(
        String publicId,
        String name,
        String slug,
        boolean active
    ) {
        return new ProfessionalProfileCategory(publicId, name, slug, active);
    }
}
