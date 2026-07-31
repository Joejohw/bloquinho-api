package com.bloquinho.professional.domain;

import java.util.List;
import java.util.Optional;

public interface ProfessionalRepository {
    List<Professional> findAllActiveByCategorySlugOrderByName(String slug);
    Optional<ProfessionalProfile> findActiveProfileByPublicId(String publicId);
}
