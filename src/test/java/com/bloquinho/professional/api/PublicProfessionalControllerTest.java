package com.bloquinho.professional.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bloquinho.professional.application.GetPublicProfessionalDetailsUseCase;
import com.bloquinho.professional.application.PublicProfessionalCategory;
import com.bloquinho.professional.application.PublicProfessionalDetails;
import com.bloquinho.shared.error.ApiExceptionHandler;
import com.bloquinho.shared.error.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringJUnitWebConfig(PublicProfessionalControllerTest.TestConfiguration.class)
class PublicProfessionalControllerTest {
    private static final String PUBLIC_ID = "Pro000000000000000002";

    @Autowired
    WebApplicationContext context;

    @Autowired
    GetPublicProfessionalDetailsUseCase useCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(useCase);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void returnsTheMinimizedPublicProfileContract() throws Exception {
        when(useCase.execute(PUBLIC_ID)).thenReturn(details());

        mockMvc.perform(get("/api/v1/public/professionals/{publicId}", PUBLIC_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.publicId").value(PUBLIC_ID))
            .andExpect(jsonPath("$.data.name").value("Lumen Instalações"))
            .andExpect(jsonPath("$.data.businessName").value("Lumen Instalações Demo"))
            .andExpect(jsonPath("$.data.whatsapp").value("5500000000002"))
            .andExpect(jsonPath("$.data.categories[0].publicId")
                .value("Ctg000000000000000009"))
            .andExpect(jsonPath("$.data.categories[0].name").value("Ar-condicionado"))
            .andExpect(jsonPath("$.data.categories[0].slug").value("ar-condicionado"))
            .andExpect(jsonPath("$.data.id").doesNotExist())
            .andExpect(jsonPath("$.data.active").doesNotExist())
            .andExpect(jsonPath("$.data.phone").doesNotExist())
            .andExpect(jsonPath("$.data.email").doesNotExist())
            .andExpect(jsonPath("$.data.createdAt").doesNotExist())
            .andExpect(jsonPath("$.data.updatedAt").doesNotExist());

        verify(useCase).execute(PUBLIC_ID);
    }

    @Test
    void returnsAnEmptyCategoryList() throws Exception {
        var details = details();
        when(useCase.execute(PUBLIC_ID)).thenReturn(new PublicProfessionalDetails(
            details.publicId(), details.name(), details.businessName(), details.description(),
            details.whatsapp(), details.instagram(), details.city(), details.state(), List.of()
        ));

        mockMvc.perform(get("/api/v1/public/professionals/{publicId}", PUBLIC_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.categories").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"short", "Pro00000000000000000!", "Pro0000000000000000 2"})
    void rejectsMalformedPublicIdWithoutCallingUseCase(String publicId) throws Exception {
        mockMvc.perform(get("/api/v1/public/professionals/{publicId}", publicId))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.type").value("about:blank"))
            .andExpect(jsonPath("$.title").value("Invalid request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Identificador público inválido."));

        verify(useCase, never()).execute(publicId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Pro000000000000000098", "Pro000000000000000099"})
    void returnsTheSameNotFoundForUnknownAndInactiveProfessional(String publicId) throws Exception {
        when(useCase.execute(publicId))
            .thenThrow(new ResourceNotFoundException("Professional not found."));

        mockMvc.perform(get("/api/v1/public/professionals/{publicId}", publicId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.title").value("Resource not found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Professional not found."));
    }

    @Test
    void returnsMethodNotAllowedForPost() throws Exception {
        mockMvc.perform(post("/api/v1/public/professionals/{publicId}", PUBLIC_ID))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(header().string("Allow", "GET"))
            .andExpect(jsonPath("$.status").value(405));
    }

    private PublicProfessionalDetails details() {
        return new PublicProfessionalDetails(
            PUBLIC_ID,
            "Lumen Instalações",
            "Lumen Instalações Demo",
            "Demonstração de serviços elétricos e instalação de climatização.",
            "5500000000002",
            "https://instagram.com/bloquinho_demo_lumen",
            "Campinas",
            "SP",
            List.of(new PublicProfessionalCategory(
                "Ctg000000000000000009", "Ar-condicionado", "ar-condicionado"
            ))
        );
    }

    @Configuration
    @EnableWebMvc
    @Import({
        PublicProfessionalController.class,
        ApiExceptionHandler.class
    })
    static class TestConfiguration {
        @Bean
        static MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }

        @Bean
        GetPublicProfessionalDetailsUseCase getPublicProfessionalDetailsUseCase() {
            return mock(GetPublicProfessionalDetailsUseCase.class);
        }
    }
}
