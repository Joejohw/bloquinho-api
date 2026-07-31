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
        assertThat(jdbcTemplate.queryForList(
            """
            SELECT version
            FROM flyway_schema_history
            WHERE success = TRUE
            ORDER BY installed_rank
            """,
            String.class
        )).containsExactly("1", "2", "3");

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

    @Test
    void findsActiveProfileWithMappedFieldsAndActiveCategoriesInNameOrder() {
        assertThat(repository.findActiveProfileByPublicId("Pro000000000000000002"))
            .hasValueSatisfying(profile -> {
                assertThat(profile.professional().publicId()).isEqualTo("Pro000000000000000002");
                assertThat(profile.professional().name()).isEqualTo("Lumen Instalações");
                assertThat(profile.professional().businessName())
                    .isEqualTo("Lumen Instalações Demo");
                assertThat(profile.professional().description())
                    .isEqualTo("Demonstração de serviços elétricos e instalação de climatização.");
                assertThat(profile.professional().whatsapp()).isEqualTo("5500000000002");
                assertThat(profile.professional().instagram())
                    .isEqualTo("https://instagram.com/bloquinho_demo_lumen");
                assertThat(profile.professional().city()).isEqualTo("Campinas");
                assertThat(profile.professional().state()).isEqualTo("SP");
                assertThat(profile.categories()).extracting("name")
                    .containsExactly("Ar-condicionado", "Elétrica");
            });
    }

    @Test
    void doesNotFindInactiveProfile() {
        jdbcTemplate.update(
            "UPDATE professionals SET active = FALSE WHERE public_id = ?",
            "Pro000000000000000002"
        );

        assertThat(repository.findActiveProfileByPublicId("Pro000000000000000002")).isEmpty();
    }

    @Test
    void excludesInactiveProfileCategories() {
        jdbcTemplate.update(
            "UPDATE professional_categories SET active = FALSE WHERE slug = ?",
            "ar-condicionado"
        );

        assertThat(repository.findActiveProfileByPublicId("Pro000000000000000002"))
            .hasValueSatisfying(profile -> assertThat(profile.categories())
                .extracting("name")
                .containsExactly("Elétrica"));
    }

    @Test
    void returnsActiveProfileWithoutCategories() {
        jdbcTemplate.update(
            """
            DELETE FROM professional_category_links
            WHERE professional_id = (
              SELECT id FROM professionals WHERE public_id = ?
            )
            """,
            "Pro000000000000000002"
        );

        assertThat(repository.findActiveProfileByPublicId("Pro000000000000000002"))
            .hasValueSatisfying(profile -> assertThat(profile.categories()).isEmpty());
    }

    @Test
    void doesNotFindUnknownProfile() {
        assertThat(repository.findActiveProfileByPublicId("Pro000000000000000099")).isEmpty();
    }
}
