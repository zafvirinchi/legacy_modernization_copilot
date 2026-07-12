package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.SecurityIssueType;
import com.ailegacy.modernization.copilot.domain.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A single detected security issue with its severity, risk score, and a
 * recommended modern Spring Security alternative. Embedded within
 * {@link SecurityAnalysisReport}, not persisted independently.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityFinding {

    private SecurityIssueType issueType;

    private String title;

    private String description;

    private Severity severity;

    private int riskScore;

    private String location;

    private String recommendation;

    private String modernAlternative;

    private List<String> evidence;

}
