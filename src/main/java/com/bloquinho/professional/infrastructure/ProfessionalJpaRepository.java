package com.bloquinho.professional.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProfessionalJpaRepository extends JpaRepository<ProfessionalJpaEntity, Long> {
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
}
