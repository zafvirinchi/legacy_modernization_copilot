package com.ailegacy.modernization.copilot.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchitecturePatternTest {

    @Test
    void parsesExactEnumTokens() {
        assertThat(ArchitecturePattern.fromLabel("MONOLITH")).isEqualTo(ArchitecturePattern.MONOLITH);
        assertThat(ArchitecturePattern.fromLabel("MVC")).isEqualTo(ArchitecturePattern.MVC);
        assertThat(ArchitecturePattern.fromLabel("LAYERED")).isEqualTo(ArchitecturePattern.LAYERED);
        assertThat(ArchitecturePattern.fromLabel("CLIENT_SERVER")).isEqualTo(ArchitecturePattern.CLIENT_SERVER);
        assertThat(ArchitecturePattern.fromLabel("MICROSERVICE")).isEqualTo(ArchitecturePattern.MICROSERVICE);
    }

    @Test
    void toleratesCommonHumanReadableVariants() {
        assertThat(ArchitecturePattern.fromLabel("Monolithic")).isEqualTo(ArchitecturePattern.MONOLITH);
        assertThat(ArchitecturePattern.fromLabel("Layered Architecture")).isEqualTo(ArchitecturePattern.LAYERED);
        assertThat(ArchitecturePattern.fromLabel("Client-Server")).isEqualTo(ArchitecturePattern.CLIENT_SERVER);
        assertThat(ArchitecturePattern.fromLabel("microservices")).isEqualTo(ArchitecturePattern.MICROSERVICE);
        assertThat(ArchitecturePattern.fromLabel("  mvc  ")).isEqualTo(ArchitecturePattern.MVC);
    }

    @Test
    void rejectsUnrecognizedLabels() {
        assertThatThrownBy(() -> ArchitecturePattern.fromLabel("Event Driven"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullLabel() {
        assertThatThrownBy(() -> ArchitecturePattern.fromLabel(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
