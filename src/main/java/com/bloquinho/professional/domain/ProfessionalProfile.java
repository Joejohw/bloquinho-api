package com.bloquinho.professional.domain;

import java.util.List;

public record ProfessionalProfile(
    Professional professional,
    List<ProfessionalProfileCategory> categories
) {
    public ProfessionalProfile {
        categories = List.copyOf(categories);
    }
}
