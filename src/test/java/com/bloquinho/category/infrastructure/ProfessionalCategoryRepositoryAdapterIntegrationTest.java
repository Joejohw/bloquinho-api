package com.bloquinho.category.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.bloquinho.category.domain.ProfessionalCategoryRepository;
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
class ProfessionalCategoryRepositoryAdapterIntegrationTest {
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
    ProfessionalCategoryRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void appliesAllCurrentMigrations() {
        assertThat(jdbcTemplate.queryForList(
            """
            SELECT version
            FROM flyway_schema_history
            WHERE success = TRUE
            ORDER BY installed_rank
            """,
            String.class
        )).containsExactly("1", "2", "3");
    }

    @Test
    void listsOnlyActiveCategoriesOrderedByName() {
        jdbcTemplate.update(
            "UPDATE professional_categories SET active = FALSE WHERE slug = ?",
            "pintura"
        );

        assertThat(repository.findAllActiveOrderByName())
            .extracting("name")
            .containsExactly(
                "Ar-condicionado",
                "Construção e reforma",
                "Elétrica",
                "Hidráulica",
                "Marcenaria",
                "Marmoraria",
                "Paisagismo",
                "Pisos e revestimentos",
                "Telhados e calhas"
            );
    }

    @Test
    void findsActiveCategoryBySlugAndMapsPublicFields() {
        assertThat(repository.findActiveBySlug("eletrica"))
            .hasValueSatisfying(category -> {
                assertThat(category.publicId()).isEqualTo("Ctg000000000000000001");
                assertThat(category.name()).isEqualTo("Elétrica");
                assertThat(category.slug()).isEqualTo("eletrica");
                assertThat(category.description())
                    .isEqualTo("Instalações, reparos e manutenção elétrica.");
                assertThat(category.active()).isTrue();
            });
    }

    @Test
    void doesNotFindInactiveCategoryBySlug() {
        jdbcTemplate.update(
            "UPDATE professional_categories SET active = FALSE WHERE slug = ?",
            "eletrica"
        );

        assertThat(repository.findActiveBySlug("eletrica")).isEmpty();
    }

    @Test
    void doesNotFindUnknownSlug() {
        assertThat(repository.findActiveBySlug("categoria-inexistente")).isEmpty();
    }
}
