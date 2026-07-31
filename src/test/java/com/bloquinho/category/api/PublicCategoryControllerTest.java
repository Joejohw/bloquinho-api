package com.bloquinho.category.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bloquinho.category.application.ListPublicCategoriesUseCase;
import com.bloquinho.category.application.GetPublicCategoryDetailsUseCase;
import com.bloquinho.category.application.PublicCategoryDetails;
import com.bloquinho.category.domain.ProfessionalCategory;
import com.bloquinho.professional.domain.Professional;
import com.bloquinho.shared.error.ApiExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PublicCategoryControllerTest {
    private final ListPublicCategoriesUseCase useCase = mock(ListPublicCategoriesUseCase.class);
    private final GetPublicCategoryDetailsUseCase detailsUseCase = mock(GetPublicCategoryDetailsUseCase.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new PublicCategoryController(useCase, detailsUseCase))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
    }

    @Test
    void returnsPublicCategoryDataWithoutInternalFields() throws Exception {
        when(useCase.execute()).thenReturn(List.of(
            new ProfessionalCategory("Ctg000000000000000001", "Elétrica", "eletrica", "Description", true)
        ));

        mockMvc.perform(get("/api/v1/public/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].publicId").value("Ctg000000000000000001"))
            .andExpect(jsonPath("$.data[0].name").value("Elétrica"))
            .andExpect(jsonPath("$.data[0].slug").value("eletrica"))
            .andExpect(jsonPath("$.data[0].id").doesNotExist())
            .andExpect(jsonPath("$.data[0].active").doesNotExist())
            .andExpect(jsonPath("$.data[0].createdAt").doesNotExist());
    }

    @Test
    void returnsHttp200WithAnEmptyDataList() throws Exception {
        when(useCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/public/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void returnsCategoryDetailsAndPublicProfessionalFields() throws Exception {
        var professional = new Professional(
            "Pro000000000000000001", "Demo Professional", "Demo Business", "Description",
            "+55 00 00000-0001", "5500000000001", "private@example.com",
            "https://instagram.com/bloquinho_demo", "Campinas", "SP", true
        );
        when(detailsUseCase.execute("eletrica")).thenReturn(new PublicCategoryDetails(
            "Ctg000000000000000001", "Elétrica", "eletrica", "Description", List.of(professional)
        ));

        mockMvc.perform(get("/api/v1/public/categories/eletrica"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.slug").value("eletrica"))
            .andExpect(jsonPath("$.data.professionals[0].publicId").value("Pro000000000000000001"))
            .andExpect(jsonPath("$.data.professionals[0].whatsapp").value("5500000000001"))
            .andExpect(jsonPath("$.data.professionals[0].id").doesNotExist())
            .andExpect(jsonPath("$.data.professionals[0].email").doesNotExist())
            .andExpect(jsonPath("$.data.professionals[0].phone").doesNotExist())
            .andExpect(jsonPath("$.data.professionals[0].active").doesNotExist());

        verify(detailsUseCase).execute("eletrica");
    }

    @Test
    void returnsHttp200WhenTheCategoryHasNoProfessionals() throws Exception {
        when(detailsUseCase.execute("eletrica")).thenReturn(new PublicCategoryDetails(
            "Ctg000000000000000001", "Elétrica", "eletrica", "Description", List.of()
        ));

        mockMvc.perform(get("/api/v1/public/categories/eletrica"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.professionals").isEmpty());
    }

    @Test
    void returnsNotFoundForAnUnknownCategory() throws Exception {
        when(detailsUseCase.execute("unknown-category"))
            .thenThrow(new com.bloquinho.shared.error.ResourceNotFoundException("Category not found."));

        mockMvc.perform(get("/api/v1/public/categories/unknown-category"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource not found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Category not found."))
            .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void returnsNotFoundForAnInactiveCategory() throws Exception {
        when(detailsUseCase.execute("inactive-category"))
            .thenThrow(new com.bloquinho.shared.error.ResourceNotFoundException("Category not found."));

        mockMvc.perform(get("/api/v1/public/categories/inactive-category"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource not found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Category not found."));
    }
}
