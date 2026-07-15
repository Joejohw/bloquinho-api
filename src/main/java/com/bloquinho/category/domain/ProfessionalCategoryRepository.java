package com.bloquinho.category.domain;

import java.util.List;

public interface ProfessionalCategoryRepository {
    List<ProfessionalCategory> findAllActiveOrderByName();
}
