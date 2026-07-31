package com.bloquinho.shared.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
class TechnicalEndpointsSecurityIntegrationTest {
    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine")
        .withDatabaseName("bloquinho")
        .withUsername("bloquinho")
        .withPassword("bloquinho");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void exposesImplementedPublicOperationsInOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openapi").isString())
            .andExpect(jsonPath("$.paths['/api/v1/public/status'].get.summary")
                .value("Get public application status"))
            .andExpect(jsonPath("$.paths['/api/v1/public/status'].post").doesNotExist())
            .andExpect(jsonPath("$.paths['/api/v1/public/status'].get.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/status'].get.responses['405']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories'].get.summary")
                .value("List active professional categories"))
            .andExpect(jsonPath("$.paths['/api/v1/public/categories'].get.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories'].get.responses['405']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}'].get.parameters[0].schema.pattern")
                .value("[a-z0-9]+(?:-[a-z0-9]+)*"))
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}'].get.parameters[0].example")
                .value("eletrica"))
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}'].get.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}'].get.responses['400']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}'].get.responses['404']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}'].get.responses['405']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/professionals/{publicId}'].get.parameters[0].schema.pattern")
                .value("[0-9A-Z_a-z-]{21}"))
            .andExpect(jsonPath("$.paths['/api/v1/public/professionals/{publicId}'].get.parameters[0].example")
                .value("Pro000000000000000002"))
            .andExpect(jsonPath("$.paths['/api/v1/public/professionals/{publicId}'].get.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/professionals/{publicId}'].get.responses['400']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/professionals/{publicId}'].get.responses['404']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/professionals/{publicId}'].get.responses['405']").exists())
            .andExpect(jsonPath("$.components.schemas.PublicProfessionalDetailsResponse.properties.email")
                .doesNotExist())
            .andExpect(jsonPath("$.components.schemas.PublicProfessionalDetailsResponse.properties.phone")
                .doesNotExist())
            .andExpect(jsonPath("$.components.schemas.PublicProfessionalDetailsResponse.properties.publicId.example")
                .value("Pro000000000000000002"))
            .andExpect(jsonPath("$.components.schemas.PublicCategoryResponse.properties.slug.example")
                .value("eletrica"))
            .andExpect(jsonPath("$.components.schemas.ProfessionalJpaEntity").doesNotExist())
            .andExpect(jsonPath("$.paths['/api/v1/admin/categories']").doesNotExist());
    }

    @Test
    void permitsSwaggerUiIndex() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Swagger UI")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/v3/api-docs", "/swagger-ui/index.html"})
    void blocksNonGetMethodsForDocumentation(String path) throws Exception {
        mockMvc.perform(post(path))
            .andExpect(status().isForbidden());
    }

    @Test
    void exposesHealthWithStatus() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").isString());
    }

    @Test
    void returnsProblemDetailForMissingPublicResource() throws Exception {
        mockMvc.perform(get("/api/v1/public/recurso-inexistente"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.type").value("about:blank"))
            .andExpect(jsonPath("$.title").value("Resource not found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("The requested resource was not found."))
            .andExpect(jsonPath("$.trace").doesNotExist())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(NoResourceFoundException.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/env", "/actuator/beans", "/actuator/configprops"})
    void doesNotExposeSensitiveActuatorEndpoints(String path) throws Exception {
        mockMvc.perform(get(path))
            .andExpect(status().isForbidden())
            .andExpect(content().string(not(containsString("propertySources"))))
            .andExpect(content().string(not(containsString("contexts"))));
    }
}
