package com.ailegacy.modernization.copilot.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModernTechnologyTest {

    @Test
    void parsesExactEnumTokens() {
        assertThat(ModernTechnology.fromLabel("SPRING_BOOT")).isEqualTo(ModernTechnology.SPRING_BOOT);
        assertThat(ModernTechnology.fromLabel("SPRING_SECURITY")).isEqualTo(ModernTechnology.SPRING_SECURITY);
        assertThat(ModernTechnology.fromLabel("DOCKER")).isEqualTo(ModernTechnology.DOCKER);
        assertThat(ModernTechnology.fromLabel("KUBERNETES")).isEqualTo(ModernTechnology.KUBERNETES);
        assertThat(ModernTechnology.fromLabel("KAFKA")).isEqualTo(ModernTechnology.KAFKA);
        assertThat(ModernTechnology.fromLabel("REDIS")).isEqualTo(ModernTechnology.REDIS);
        assertThat(ModernTechnology.fromLabel("OPENAPI")).isEqualTo(ModernTechnology.OPENAPI);
        assertThat(ModernTechnology.fromLabel("CLOUD_MIGRATION")).isEqualTo(ModernTechnology.CLOUD_MIGRATION);
    }

    @Test
    void toleratesCommonVariants() {
        assertThat(ModernTechnology.fromLabel("Spring Boot")).isEqualTo(ModernTechnology.SPRING_BOOT);
        assertThat(ModernTechnology.fromLabel("k8s")).isEqualTo(ModernTechnology.KUBERNETES);
        assertThat(ModernTechnology.fromLabel("Swagger")).isEqualTo(ModernTechnology.OPENAPI);
        assertThat(ModernTechnology.fromLabel("Cloud")).isEqualTo(ModernTechnology.CLOUD_MIGRATION);
    }

    @Test
    void rejectsUnrecognizedLabels() {
        assertThatThrownBy(() -> ModernTechnology.fromLabel("GraphQL"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullLabel() {
        assertThatThrownBy(() -> ModernTechnology.fromLabel(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
