package com.bloquinho.shared.id;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class PublicIdGeneratorTest {
    @Test void generatesUrlSafeUniqueLookingValues() {
        var generator = new PublicIdGenerator(); var sample = new HashSet<String>();
        for (int i = 0; i < 1_000; i++) { var id = generator.generate(); assertThat(id).hasSize(21).matches("[0-9A-Za-z_-]{21}"); sample.add(id); }
        assertThat(sample).hasSize(1_000);
    }
}
