package com.bloquinho.category.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bloquinho.category.application.ListPublicCategoriesUseCase;
import com.bloquinho.category.domain.ProfessionalCategory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PublicCategoryControllerTest {
    private final ListPublicCategoriesUseCase useCase = mock(ListPublicCategoriesUseCase.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PublicCategoryController(useCase)).build();
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
}
