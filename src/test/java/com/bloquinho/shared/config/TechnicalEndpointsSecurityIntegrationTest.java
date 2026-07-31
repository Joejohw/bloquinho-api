package com.bloquinho.shared.config;

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
            .andExpect(jsonPath("$.paths['/api/v1/public/status']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories']").exists())
            .andExpect(jsonPath("$.paths['/api/v1/public/categories/{slug}']").exists())
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

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/env", "/actuator/beans", "/actuator/configprops"})
    void doesNotExposeSensitiveActuatorEndpoints(String path) throws Exception {
        mockMvc.perform(get(path))
            .andExpect(status().isForbidden())
            .andExpect(content().string(not(containsString("propertySources"))))
            .andExpect(content().string(not(containsString("contexts"))));
    }
}
