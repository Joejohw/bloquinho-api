package com.bloquinho.shared.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bloquinho.catalog.api.PublicStatusController;
import com.bloquinho.category.api.PublicCategoryController;
import com.bloquinho.category.application.GetPublicCategoryDetailsUseCase;
import com.bloquinho.category.application.ListPublicCategoriesUseCase;
import com.bloquinho.category.application.PublicCategoryDetails;
import com.bloquinho.shared.error.ApiExceptionHandler;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringJUnitWebConfig(PublicApiSecurityIntegrationTest.TestConfiguration.class)
class PublicApiSecurityIntegrationTest {
    @Autowired
    WebApplicationContext context;

    @Autowired
    ListPublicCategoriesUseCase listCategories;

    @Autowired
    GetPublicCategoryDetailsUseCase categoryDetails;

    @Autowired
    InMemoryUserDetailsManager users;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(listCategories, categoryDetails);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void permitsPublicStatus() throws Exception {
        mockMvc.perform(get("/api/v1/public/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void permitsPublicCategoryList() throws Exception {
        when(listCategories.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/public/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void permitsValidPublicCategoryDetails() throws Exception {
        when(categoryDetails.execute("eletrica")).thenReturn(new PublicCategoryDetails(
            "Ctg000000000000000001", "Elétrica", "eletrica", "Description", List.of()
        ));

        mockMvc.perform(get("/api/v1/public/categories/eletrica"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.slug").value("eletrica"));

        verify(categoryDetails).execute("eletrica");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Elétrica", "slug com espaço", "slug!"})
    void rejectsMalformedSlugAsProblemDetail(String slug) throws Exception {
        mockMvc.perform(get("/api/v1/public/categories/{slug}", slug))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.type").value("about:blank"))
            .andExpect(jsonPath("$.title").value("Invalid request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Slug inválido."))
            .andExpect(jsonPath("$.trace").doesNotExist());

        verify(categoryDetails, never()).execute(slug);
    }

    @Test
    void blocksAdministrativeGet() throws Exception {
        mockMvc.perform(get("/api/v1/admin/categories"))
            .andExpect(status().isForbidden());
    }

    @Test
    void blocksAdministrativePost() throws Exception {
        mockMvc.perform(post("/api/v1/admin/categories"))
            .andExpect(status().isForbidden());
    }

    @Test
    void blocksUnknownAdministrativeRoute() throws Exception {
        mockMvc.perform(get("/api/v1/admin/not-mapped"))
            .andExpect(status().isForbidden());
    }

    @Test
    void blocksUnknownRouteOutsidePublicPrefixes() throws Exception {
        mockMvc.perform(get("/not-mapped"))
            .andExpect(status().isForbidden());
    }

    @Test
    void leavesUnmappedPublicRouteToMvc() throws Exception {
        mockMvc.perform(get("/api/v1/public/not-mapped"))
            .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    @Test
    void doesNotConvertUnsupportedPublicMethodToForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/public/categories"))
            .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    @Test
    void hasNoDefaultUser() {
        assertThatThrownBy(() -> users.loadUserByUsername("user"))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void permitsConfiguredCorsPreflight() throws Exception {
        mockMvc.perform(options("/api/v1/public/categories")
                .header("Origin", "http://localhost:4300")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization, Content-Type"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4300"))
            .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS"))
            .andExpect(header().string("Access-Control-Allow-Headers", containsString("Authorization")))
            .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")))
            .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }

    @Test
    void permitsConfiguredAdministrativeOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/admin/categories")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void doesNotAuthorizeUnknownCorsOrigin() throws Exception {
        mockMvc.perform(get("/api/v1/public/status")
                .header("Origin", "https://unknown.example"))
            .andExpect(status().isForbidden())
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void rejectsPreflightFromUnknownOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/public/categories")
                .header("Origin", "https://unknown.example")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isForbidden())
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({
        SecurityConfig.class,
        ApiExceptionHandler.class,
        PublicCategoryController.class,
        PublicStatusController.class
    })
    static class TestConfiguration {
        @Bean
        static PropertySourcesPlaceholderConfigurer properties() {
            var values = new Properties();
            values.setProperty("app.cors.admin-origin", "http://localhost:4200");
            values.setProperty("app.cors.public-origin", "http://localhost:4300");
            var configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setProperties(values);
            return configurer;
        }

        @Bean
        static MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }

        @Bean
        ListPublicCategoriesUseCase listCategories() {
            return mock(ListPublicCategoriesUseCase.class);
        }

        @Bean
        GetPublicCategoryDetailsUseCase categoryDetails() {
            return mock(GetPublicCategoryDetailsUseCase.class);
        }
    }
}
