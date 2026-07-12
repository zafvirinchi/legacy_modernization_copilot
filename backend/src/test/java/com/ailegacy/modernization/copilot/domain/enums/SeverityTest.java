package com.ailegacy.modernization.copilot.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeverityTest {

    @Test
    void parsesExactEnumTokens() {
        assertThat(Severity.fromLabel("LOW")).isEqualTo(Severity.LOW);
        assertThat(Severity.fromLabel("MEDIUM")).isEqualTo(Severity.MEDIUM);
        assertThat(Severity.fromLabel("HIGH")).isEqualTo(Severity.HIGH);
        assertThat(Severity.fromLabel("CRITICAL")).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void toleratesCommonVariants() {
        assertThat(Severity.fromLabel("med")).isEqualTo(Severity.MEDIUM);
        assertThat(Severity.fromLabel("Moderate")).isEqualTo(Severity.MEDIUM);
        assertThat(Severity.fromLabel("severe")).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void defaultsToMediumForUnrecognizedOrMissingLabels() {
        assertThat(Severity.fromLabel("Unknown")).isEqualTo(Severity.MEDIUM);
        assertThat(Severity.fromLabel(null)).isEqualTo(Severity.MEDIUM);
    }

}
