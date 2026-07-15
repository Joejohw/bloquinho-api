package com.bloquinho.professional.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.bloquinho.professional.domain.ProfessionalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
@Transactional
class ProfessionalRepositoryAdapterIntegrationTest {
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
    ProfessionalRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void appliesV3AndFiltersActiveRelationshipsInNameOrder() {
        assertThat(jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM flyway_schema_history WHERE version = '3' AND success = TRUE",
            Integer.class
        )).isEqualTo(1);

        var initial = repository.findAllActiveByCategorySlugOrderByName("eletrica");
        assertThat(initial).extracting("name")
            .containsExactly("Carlos Elétrica Residencial", "Lumen Instalações");

        jdbcTemplate.update(
            "UPDATE professionals SET active = FALSE WHERE public_id = ?",
            "Pro000000000000000001"
        );
        assertThat(repository.findAllActiveByCategorySlugOrderByName("eletrica"))
            .extracting("name")
            .containsExactly("Lumen Instalações");

        jdbcTemplate.update(
            "UPDATE professional_categories SET active = FALSE WHERE slug = ?",
            "eletrica"
        );
        assertThat(repository.findAllActiveByCategorySlugOrderByName("eletrica")).isEmpty();
    }
}
