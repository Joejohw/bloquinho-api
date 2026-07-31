package com.bloquinho.shared.id;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class PublicIdGeneratorTest {
    @Test
    void generatesUrlSafeValuesWithTheExpectedLength() {
        var generator = new PublicIdGenerator();
        var sample = new HashSet<String>();

        for (int i = 0; i < 1_000; i++) {
            var id = generator.generate();
            assertThat(id).hasSize(PublicIdGenerator.LENGTH).matches(PublicIdGenerator.PATTERN);
            sample.add(id);
        }

        assertThat(sample).hasSize(1_000);
    }

    @Test
    void returnsDifferentValuesAcrossCalls() {
        var generator = new PublicIdGenerator();
        assertThat(generator.generate()).isNotEqualTo(generator.generate());
    }
}
