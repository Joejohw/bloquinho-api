package com.bloquinho.professional.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProfessionalJpaRepository extends JpaRepository<ProfessionalJpaEntity, Long> {
    Optional<ProfessionalJpaEntity> findByPublicIdAndActiveTrue(String publicId);

    @Query(value = """
        SELECT p.*
        FROM professionals p
        JOIN professional_category_links pcl ON pcl.professional_id = p.id
        JOIN professional_categories pc ON pc.id = pcl.category_id
        WHERE pc.slug = :slug
          AND pc.active = TRUE
          AND p.active = TRUE
        ORDER BY p.name ASC
        """, nativeQuery = true)
    List<ProfessionalJpaEntity> findAllActiveByCategorySlugOrderByName(@Param("slug") String slug);

    @Query(value = """
        SELECT
          pc.public_id AS "publicId",
          pc.name AS "name",
          pc.slug AS "slug",
          pc.active AS "active"
        FROM professional_categories pc
        JOIN professional_category_links pcl ON pcl.category_id = pc.id
        JOIN professionals p ON p.id = pcl.professional_id
        WHERE p.public_id = :publicId
          AND p.active = TRUE
          AND pc.active = TRUE
        ORDER BY pc.name ASC
        """, nativeQuery = true)
    List<ProfessionalProfileCategoryView> findAllActiveProfileCategoriesOrderByName(
        @Param("publicId") String publicId
    );
}
