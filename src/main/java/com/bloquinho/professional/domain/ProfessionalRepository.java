package com.bloquinho.professional.domain;

import java.util.List;

public interface ProfessionalRepository {
    List<Professional> findAllActiveByCategorySlugOrderByName(String slug);
}
