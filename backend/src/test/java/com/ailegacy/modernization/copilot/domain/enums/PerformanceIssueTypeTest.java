package com.ailegacy.modernization.copilot.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PerformanceIssueTypeTest {

    @Test
    void parsesExactEnumTokens() {
        assertThat(PerformanceIssueType.fromLabel("N_PLUS_ONE_QUERY")).isEqualTo(PerformanceIssueType.N_PLUS_ONE_QUERY);
        assertThat(PerformanceIssueType.fromLabel("LARGE_CLASS")).isEqualTo(PerformanceIssueType.LARGE_CLASS);
        assertThat(PerformanceIssueType.fromLabel("GOD_OBJECT")).isEqualTo(PerformanceIssueType.GOD_OBJECT);
        assertThat(PerformanceIssueType.fromLabel("MEMORY_LEAK_RISK")).isEqualTo(PerformanceIssueType.MEMORY_LEAK_RISK);
        assertThat(PerformanceIssueType.fromLabel("DUPLICATE_CODE")).isEqualTo(PerformanceIssueType.DUPLICATE_CODE);
        assertThat(PerformanceIssueType.fromLabel("BLOCKING_IO")).isEqualTo(PerformanceIssueType.BLOCKING_IO);
    }

    @Test
    void toleratesCommonHumanReadableVariants() {
        assertThat(PerformanceIssueType.fromLabel("N+1 Queries")).isEqualTo(PerformanceIssueType.N_PLUS_ONE_QUERY);
        assertThat(PerformanceIssueType.fromLabel("God Class")).isEqualTo(PerformanceIssueType.GOD_OBJECT);
        assertThat(PerformanceIssueType.fromLabel("Memory Leaks")).isEqualTo(PerformanceIssueType.MEMORY_LEAK_RISK);
        assertThat(PerformanceIssueType.fromLabel("Code Duplication")).isEqualTo(PerformanceIssueType.DUPLICATE_CODE);
        assertThat(PerformanceIssueType.fromLabel("synchronous io")).isEqualTo(PerformanceIssueType.BLOCKING_IO);
    }

    @Test
    void rejectsUnrecognizedLabels() {
        assertThatThrownBy(() -> PerformanceIssueType.fromLabel("Spaghetti Code"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNullLabel() {
        assertThatThrownBy(() -> PerformanceIssueType.fromLabel(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
