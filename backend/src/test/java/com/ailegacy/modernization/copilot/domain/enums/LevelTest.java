package com.ailegacy.modernization.copilot.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevelTest {

    @Test
    void parsesExactEnumTokens() {
        assertThat(Level.fromLabel("LOW")).isEqualTo(Level.LOW);
        assertThat(Level.fromLabel("MEDIUM")).isEqualTo(Level.MEDIUM);
        assertThat(Level.fromLabel("HIGH")).isEqualTo(Level.HIGH);
    }

    @Test
    void toleratesCommonVariants() {
        assertThat(Level.fromLabel("med")).isEqualTo(Level.MEDIUM);
        assertThat(Level.fromLabel("Moderate")).isEqualTo(Level.MEDIUM);
    }

    @Test
    void defaultsToMediumForUnrecognizedOrMissingLabels() {
        assertThat(Level.fromLabel("Extreme")).isEqualTo(Level.MEDIUM);
        assertThat(Level.fromLabel(null)).isEqualTo(Level.MEDIUM);
    }

}
