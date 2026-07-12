package com.ailegacy.modernization.copilot.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityIssueTypeTest {

    @Test
    void parsesExactEnumTokens() {
        assertThat(SecurityIssueType.fromLabel("SQL_INJECTION")).isEqualTo(SecurityIssueType.SQL_INJECTION);
        assertThat(SecurityIssueType.fromLabel("HARDCODED_PASSWORD")).isEqualTo(SecurityIssueType.HARDCODED_PASSWORD);
        assertThat(SecurityIssueType.fromLabel("WEAK_ENCRYPTION")).isEqualTo(SecurityIssueType.WEAK_ENCRYPTION);
        assertThat(SecurityIssueType.fromLabel("MISSING_AUTHENTICATION")).isEqualTo(SecurityIssueType.MISSING_AUTHENTICATION);
        assertThat(SecurityIssueType.fromLabel("SESSION_RISK")).isEqualTo(SecurityIssueType.SESSION_RISK);
        assertThat(SecurityIssueType.fromLabel("OWASP_ISSUE")).isEqualTo(SecurityIssueType.OWASP_ISSUE);
    }

    @Test
    void toleratesCommonHumanReadableVariants() {
        assertThat(SecurityIssueType.fromLabel("SQLi")).isEqualTo(SecurityIssueType.SQL_INJECTION);
        assertThat(SecurityIssueType.fromLabel("Hardcoded Passwords")).isEqualTo(SecurityIssueType.HARDCODED_PASSWORD);
        assertThat(SecurityIssueType.fromLabel("Broken Authentication")).isEqualTo(SecurityIssueType.MISSING_AUTHENTICATION);
        assertThat(SecurityIssueType.fromLabel("session risks")).isEqualTo(SecurityIssueType.SESSION_RISK);
    }

    @Test
    void fallsBackToOwaspIssueCatchAllForUnrecognizedLabels() {
        assertThat(SecurityIssueType.fromLabel("Insecure Deserialization")).isEqualTo(SecurityIssueType.OWASP_ISSUE);
        assertThat(SecurityIssueType.fromLabel(null)).isEqualTo(SecurityIssueType.OWASP_ISSUE);
    }

}
